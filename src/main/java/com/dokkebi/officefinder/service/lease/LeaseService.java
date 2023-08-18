package com.dokkebi.officefinder.service.lease;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.LeaseRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseLookUpServiceResponse;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeServiceResponse;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaseService {

  private final LeaseRepository leaseRepository;
  private final CustomerRepository customerRepository;
  private final OfficeRepository officeRepository;

  private final ReviewRepository reviewRepository;

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

    long totalPrice = leaseOfficeRequestDto.getMonths() * office.getLeaseFee();
    checkCustomerPoints(customer, totalPrice);

    customer.usePoint(totalPrice);

    Lease lease = saveLeaseInfo(customer, office, totalPrice, leaseOfficeRequestDto);

    return LeaseOfficeServiceResponse.of(lease);
  }

  private Lease saveLeaseInfo(Customer customer, Office office, long totalPrice,
      LeaseOfficeRequestDto request) {

    Lease lease = Lease.builder()
        .customer(customer)
        .office(office)
        .price(totalPrice)
        .leaseStatus(LeaseStatus.AWAIT)
        .leaseStartDate(request.getStartDate())
        .leaseEndDate(request.getStartDate().plusMonths(request.getMonths()))
        .isMonthlyPay(request.isMonthlyPay())
        .build();

    leaseRepository.save(lease);

    return lease;
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

  @Transactional(readOnly = true)
  public Page<LeaseLookUpServiceResponse> lookupLease(String email, Pageable pageable) {
    Customer customer = customerRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_REGISTERED));

    Page<Lease> leases = leaseRepository.findByCustomerId(customer.getId(), pageable);

    return leases.map(lease -> LeaseLookUpServiceResponse.of(lease,
        reviewRepository.existsByLeaseId(lease.getId())));
  }
}
