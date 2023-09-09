package com.dokkebi.officefinder.service.lease;

import static com.dokkebi.officefinder.exception.CustomErrorCode.EMAIL_NOT_REGISTERED;
import static com.dokkebi.officefinder.exception.CustomErrorCode.INSUFFICIENT_POINTS;
import static com.dokkebi.officefinder.exception.CustomErrorCode.INVALID_OFFICE_ID;
import static com.dokkebi.officefinder.exception.CustomErrorCode.LEASE_NOT_FOUND;
import static com.dokkebi.officefinder.exception.CustomErrorCode.NO_ROOMS_AVAILABLE_FOR_LEASE;
import static com.dokkebi.officefinder.exception.CustomErrorCode.OFFICE_NOT_OWNED_BY_OWNER;
import static com.dokkebi.officefinder.exception.CustomErrorCode.OFFICE_OVER_CAPACITY;
import static com.dokkebi.officefinder.exception.CustomErrorCode.OWNER_NOT_FOUND;

import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.AgentLeaseLookUpResponse;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.entity.type.NotificationType;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.repository.office.picture.OfficePictureRepository;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseLookUpServiceResponse;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeRequestDto;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeServiceResponse;
import com.dokkebi.officefinder.service.notification.NotificationService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaseService {

  private final OfficeOwnerRepository officeOwnerRepository;

  private final LeaseRepository leaseRepository;
  private final CustomerRepository customerRepository;
  private final OfficeRepository officeRepository;
  private final ReviewRepository reviewRepository;
  private final OfficePictureRepository officePictureRepository;
  private final NotificationService notificationService;

  /**
   * 오피스 임대 서비스를 처리하는 메서드입니다. 고객의 이메일을 통해 고객의 정보를 조회하고, 고객이 임대를 요청한 오피스 정보를 조회합니다. 오피스의 이용 개월 수와
   * 임대료를 통해 전체 가격을 계산하고, 고객의 포인트를 조회한 후 임대가 가능한지 확인합니다. 모든 조건이 충족되면 임대 정보를 저장하고 반환합니다.
   *
   * @param leaseOfficeRequestDto
   * @return LeaseServiceResponse
   * @Throws CustomException 발생 - EMAIL_NOT_REGISTERED : 등록되지 않은 이메일로 요청한 경우 - INVALID_OFFICE_ID :
   * 유효하지 않은 오피스 ID로 요청한 경우 - OFFICE_OVER_CAPACITY : 요청 인원이 오피스의 수용인원을 초과하는 경우 - INSUFFICIENT_POINTS
   * : 고객의 포인트가 부족한경우
   */
  @Transactional
  public LeaseOfficeServiceResponse leaseOffice(LeaseOfficeRequestDto leaseOfficeRequestDto) {
    Customer customer = customerRepository.findByEmail(leaseOfficeRequestDto.getEmail())
        .orElseThrow(() -> new CustomException(EMAIL_NOT_REGISTERED));

    Office office = officeRepository.findById(leaseOfficeRequestDto.getOfficeId())
        .orElseThrow(() -> new CustomException(INVALID_OFFICE_ID));

    checkAvailableRooms(leaseOfficeRequestDto, office.getMaxRoomCount());
    checkOfficeCapacity(office, leaseOfficeRequestDto.getCustomerCount());

    long totalPrice = leaseOfficeRequestDto.getMonths() * office.getLeaseFee();
    checkCustomerPoints(customer, totalPrice);

    customer.usePoint(totalPrice);

    Lease lease = Lease.fromRequest(customer, office, totalPrice, leaseOfficeRequestDto);
    Lease savedLease = leaseRepository.save(lease);

    notificationService.sendToOwner(office.getOwner(), NotificationType.LEASE_REQUEST_ARRIVED,
        "임대 요청", office.getName() + "에 임대 요청이 들어왔습니다");

    return LeaseOfficeServiceResponse.of(savedLease);
  }

  public Page<LeaseLookUpServiceResponse> getLeaseList(String email, Pageable pageable) {
    Customer customer = customerRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(EMAIL_NOT_REGISTERED));

    Page<Lease> leases = leaseRepository.findByCustomerId(customer.getId(), pageable);

    return leases.map(lease -> LeaseLookUpServiceResponse.of(lease,
        reviewRepository.existsByLeaseId(lease.getId()), officePictureRepository.findByOfficeId(lease.getOffice().getId())));
  }

  // 특정 오피스에 들어온 임대 요청 정보 확인(AWAIT 상태의 임대 정보만)
  public Page<AgentLeaseLookUpResponse> getLeaseRequestList(String email, Long officeId,
      Pageable pageable) {
    OfficeOwner officeOwner = officeOwnerRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(OWNER_NOT_FOUND));

    Office office = officeRepository.findByOwnerAndId(officeOwner, officeId)
        .orElseThrow(() -> new CustomException(OFFICE_NOT_OWNED_BY_OWNER));

    // 수략 대기 상태에 있는 Lease 정보들을 가져옴
    Page<Lease> awaitList = leaseRepository.findByOfficeIdAndLeaseStatus(office.getId(),
        LeaseStatus.AWAIT, pageable);

    return awaitList.map(l -> AgentLeaseLookUpResponse.of(l, office.getName()));
  }

  public void acceptLeaseRequest(Long leaseId) {
    Lease lease = leaseRepository.findByLeaseId(leaseId)
        .orElseThrow(() -> new CustomException(LEASE_NOT_FOUND));

    // 변경 후 저장안해도 더티 체킹으로 인해 반영됨
    lease.changeLeaseStatus(LeaseStatus.ACCEPTED);

    notificationService.sendToCustomer(lease.getCustomer(), NotificationType.LEASE_ACCEPTED,
        "임대 요청 수락",
        lease.getOffice().getName() + "에 대한 임대 요청이 수락되었습니다.");
  }

  @Transactional
  public void rejectLeaseRequest(Long leaseId) {
    Lease lease = leaseRepository.findByLeaseId(leaseId)
        .orElseThrow(() -> new CustomException(LEASE_NOT_FOUND));

    // 임대 거절시 포인트를 다시 환급
    refundPayment(lease.getCustomer(), lease.getPrice());

    // 거절 상태로 바꿈
    lease.changeLeaseStatus(LeaseStatus.DENIED);

    notificationService.sendToCustomer(lease.getCustomer(), NotificationType.LEASE_DENIED,
        "임대 요청 거절",
        lease.getOffice().getName() + "에 대한 임대 요청이 거절되었습니다.");
  }

  private void refundPayment(Customer customer, long price) {
    customer.chargePoint(price);
  }

  private void checkOfficeCapacity(Office office, int customerCount) {
    if (office.getMaxCapacity() < customerCount) {
      throw new CustomException(OFFICE_OVER_CAPACITY);
    }
  }

  private void checkCustomerPoints(Customer customer, long totalPrice) {
    if (customer.getPoint() < totalPrice) {
      throw new CustomException(INSUFFICIENT_POINTS);
    }
  }

  private void checkAvailableRooms(LeaseOfficeRequestDto office, int maxRoomCount) {
    LocalDate endDate = office.getStartDate().plusMonths(office.getMonths());
    List<LeaseStatus> leaseStatus = Arrays.asList(LeaseStatus.AWAIT, LeaseStatus.ACCEPTED);

    Long roomUsed = leaseRepository.countOfficeRoomInUse(office.getOfficeId(), leaseStatus,
        office.getStartDate(), endDate);

    if (roomUsed != null && roomUsed >= maxRoomCount) {
      throw new CustomException(NO_ROOMS_AVAILABLE_FOR_LEASE);
    }
  }
}
