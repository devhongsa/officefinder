package com.dokkebi.officefinder.service.lease;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseLookUpServiceResponse;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeRequestDto;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeServiceResponse;
import com.dokkebi.officefinder.service.notification.NotificationService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaseService {

  private final LeaseRepository leaseRepository;
  private final CustomerRepository customerRepository;
  private final OfficeRepository officeRepository;
  private final ReviewRepository reviewRepository;
  private final RedissonClient redissonClient;

  private final NotificationService notificationService;

  /**
   * 오피스 임대 서비스를 처리하는 메서드입니다.
   * 고객의 이메일을 통해 고객의 정보를 조회하고, 고객이 임대를 요청한 오피스 정보를 조회합니다.
   * 오피스의 이용 개월 수와 임대료를 통해 전체 가격을 계산하고, 고객의 포인트를 조회한 후 임대가 가능한지 확인합니다.
   * 모든 조건이 충족되면 임대 정보를 저장하고 반환합니다.
   *
   * @param leaseOfficeRequestDto
   * @return LeaseServiceResponse
   * @Throws CustomException 발생
   *             - EMAIL_NOT_REGISTERED : 등록되지 않은 이메일로 요청한 경우
   *             - INVALID_OFFICE_ID : 유효하지 않은 오피스 ID로 요청한 경우
   *             - OFFICE_OVER_CAPACITY : 요청 인원이 오피스의 수용인원을 초과하는 경우
   *             - INSUFFICIENT_POINTS : 고객의 포인트가 부족한경우
   */
  @Transactional
  public LeaseOfficeServiceResponse leaseOffice(LeaseOfficeRequestDto leaseOfficeRequestDto) {
    Customer customer = customerRepository.findByEmail(leaseOfficeRequestDto.getEmail())
        .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_REGISTERED));

    Office office = officeRepository.findById(leaseOfficeRequestDto.getOfficeId())
        .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_OFFICE_ID));

    checkOfficeCapacity(office, leaseOfficeRequestDto.getCustomerCount());
    decreaseRemainOffice(office);

    long totalPrice = leaseOfficeRequestDto.getMonths() * office.getLeaseFee();
    checkCustomerPoints(customer, totalPrice);

    customer.usePoint(totalPrice);

    Lease lease = Lease.fromRequest(customer, office, totalPrice, leaseOfficeRequestDto);
    Lease savedLease = leaseRepository.save(lease);

    notificationService.sendLeaseNotification(office);

    return LeaseOfficeServiceResponse.of(savedLease);
  }

  public Page<LeaseLookUpServiceResponse> getLeaseList(String email, Pageable pageable) {
    Customer customer = customerRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_REGISTERED));

    Page<Lease> leases = leaseRepository.findByCustomerId(customer.getId(), pageable);

    return leases.map(lease -> LeaseLookUpServiceResponse.of(lease,
        reviewRepository.existsByLeaseId(lease.getId())));
  }

  private void checkOfficeCapacity(Office office, int customerCount) {
    if (office.getMaxCapacity() < customerCount) {
      throw new CustomException(CustomErrorCode.OFFICE_OVER_CAPACITY);
    }
  }

  private void checkCustomerPoints(Customer customer, long totalPrice) {
    if (customer.getPoint() < totalPrice) {
      throw new CustomException(CustomErrorCode.INSUFFICIENT_POINTS);
    }
  }

  private void decreaseRemainOffice(Office office) {
    final String lockName = "remainRoom:lock";
    final RLock lock = redissonClient.getLock(lockName);

    try{
      if (!lock.tryLock(1, 3, TimeUnit.SECONDS)) {
        throw new IllegalArgumentException("lock exception");
      }
      office.decreaseRemainRoom();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      if (lock != null && lock.isLocked()){
        lock.unlock();
      }
    }
  }
}
