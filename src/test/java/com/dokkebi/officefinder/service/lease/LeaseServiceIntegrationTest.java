package com.dokkebi.officefinder.service.lease;

import static com.dokkebi.officefinder.entity.type.LeaseStatus.AWAIT;
import static org.assertj.core.api.Assertions.assertThat;

import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeRequestDto;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeServiceResponse;
import com.dokkebi.officefinder.service.office.OfficeService;
import java.time.LocalDate;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
public class LeaseServiceIntegrationTest {

  @Autowired
  private OfficeService officeService;
  @Autowired
  private LeaseService leaseService;
  @Autowired
  private OfficeOwnerRepository officeOwnerRepository;
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  private static final String REMAIN_ROOM_KEY = "remain-room";

  @AfterEach
  void tearDown() {
    redisTemplate.delete(REMAIN_ROOM_KEY);
  }

  @DisplayName("임대 계약을 수행한다. 결재 시 가격만큼 회원의 포인트가 감소하고 오피스 주인의 포인트가 증가한다.")
  @Test
  public void createLeaseData() {
    // given
    Customer savedCustomer = createCustomer("customer1", "test@test.com", "1234",
        Set.of("ROLE_CUSTOMER"), 1000000);

    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));
    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);

    // set office create request dto
    OfficeCreateRequestDto request = new OfficeCreateRequestDto();
    setOfficeInfo(request, "office1", 5, 500000, 5);
    setOfficeLocation(request, "경상남도", "김해시", "삼계동", "", "삼계로", 12345, 127.1315, 37.4562);
    setOfficeCondition(request, false, false, true, true, true, true,
        true, true, true, true, true, true, true, true, true);

    Long savedId = officeService.createOfficeInfo(request, savedOfficeOwner.getEmail());

    // set lease data
    LocalDate leaseDate = LocalDate.now();
    LeaseOfficeRequestDto leaseRequest = createLeaseRequest("test@test.com", savedId, leaseDate, 1,
        4, false);

    // when
    LeaseOfficeServiceResponse response = leaseService.leaseOffice(leaseRequest);

    // then
    assertThat(response)
        .extracting("customerEmail", "officeName", "price", "leaseStatus", "startDate", "endDate")
        .contains(
            "test@test.com", "office1", 500000L, AWAIT, leaseDate, leaseDate.plusMonths(1)
        );

    assertThat(savedCustomer.getPoint()).isEqualTo(500000L);
  }

  @DisplayName("회원이 진행중이거나 진행했던 임대 계약을 페이징으로 조회할 수 있다.")
  @Test
  public void getLeaseInfoList() {
    // given
    Customer savedCustomer = createCustomer("customer1", "test@test.com", "1234",
        Set.of("ROLE_CUSTOMER"), 1000000);

    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));
    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);

    // set office create request dto
    OfficeCreateRequestDto request = new OfficeCreateRequestDto();
    setOfficeInfo(request, "office1", 5, 500000, 5);
    setOfficeLocation(request, "경상남도", "김해시", "삼계동", "", "삼계로", 12345, 127.1315, 37.4562);
    setOfficeCondition(request, false, false, true, true, true, true,
        true, true, true, true, true, true, true, true, true);

    Long savedId = officeService.createOfficeInfo(request, savedOfficeOwner.getEmail());


    // set lease data
    LocalDate leaseDate = LocalDate.now().minusDays(30);
    LocalDate leaseDate2 = LocalDate.now();

    LeaseOfficeRequestDto leaseRequest = createLeaseRequest("test@test.com", savedId, leaseDate, 1,
        4, false);
    LeaseOfficeRequestDto leaseRequest2 = createLeaseRequest("test@test.com", savedId, leaseDate2, 1,
        4, false);
    LeaseOfficeServiceResponse response = leaseService.leaseOffice(leaseRequest);
    LeaseOfficeServiceResponse response2 = leaseService.leaseOffice(leaseRequest2);

    // when

    // then
    assertThat(response)
        .extracting("customerEmail", "officeName", "price", "leaseStatus", "startDate", "endDate")
        .contains(
            "test@test.com", "office1", 500000L, AWAIT, leaseDate, leaseDate.plusMonths(1)
        );
  }

  private LeaseOfficeRequestDto createLeaseRequest(String email, Long officeId, LocalDate startDate,
      int months, int customerCount, boolean isMonthlyPay) {

    return LeaseOfficeRequestDto.builder()
        .email(email)
        .officeId(officeId)
        .startDate(startDate)
        .months(months)
        .customerCount(customerCount)
        .isMonthlyPay(isMonthlyPay)
        .build();
  }

  private void createReview(Customer customer, Office office, Lease lease, int rate,
      String description) {
    reviewRepository.save(Review.builder()
        .customer(customer)
        .office(office)
        .lease(lease)
        .rate(rate)
        .description(description)
        .build());
  }

  private OfficeOwner createOfficeOwner(String name, String email, String password,
      String businessNumber, long point, Set<String> roles) {

    return OfficeOwner.builder()
        .name(name)
        .email(email)
        .password(password)
        .businessNumber(businessNumber)
        .point(point)
        .roles(roles)
        .build();
  }

  private Customer createCustomer(String name, String email, String password, Set<String> roles,
      int point) {
    return customerRepository.save(Customer.builder()
        .name(name)
        .email(email)
        .password(password)
        .roles(roles)
        .point(point)
        .build());
  }

  private void setOfficeInfo(OfficeCreateRequestDto request, String officeName, int maxCapacity,
      long leaseFee, int remainRoom) {
    request.setOfficeName(officeName);
    request.setMaxCapacity(maxCapacity);
    request.setLeaseFee(leaseFee);
    request.setRemainRoom(remainRoom);
  }

  private void setOfficeLocation(OfficeCreateRequestDto request, String legion, String city,
      String town, String village, String street, int zipcode, double latitude, double longitude) {

    request.setLegion(legion);
    request.setCity(city);
    request.setTown(town);
    request.setVillage(village);
    request.setStreet(street);
    request.setZipcode(zipcode);
    request.setLatitude(latitude);
    request.setLongitude(longitude);
  }

  private void setOfficeCondition(OfficeCreateRequestDto request, boolean airCondition,
      boolean heaterCondition, boolean cafe,
      boolean printer, boolean packageSendService, boolean doorLock, boolean fax,
      boolean publicKitchen, boolean publicLounge, boolean privateLocker, boolean tvProjector,
      boolean whiteboard, boolean wifi, boolean showerBooth, boolean storage) {

    request.setHaveAirCondition(airCondition);
    request.setHaveHeater(heaterCondition);
    request.setHaveCafe(cafe);
    request.setHavePrinter(printer);
    request.setPackageSendServiceAvailable(packageSendService);
    request.setHaveDoorLock(doorLock);
    request.setFaxServiceAvailable(fax);
    request.setHavePublicKitchen(publicKitchen);
    request.setHavePublicLounge(publicLounge);
    request.setHavePrivateLocker(privateLocker);
    request.setHaveTvProjector(tvProjector);
    request.setHaveWhiteBoard(whiteboard);
    request.setHaveWifi(wifi);
    request.setHaveShowerBooth(showerBooth);
    request.setHaveStorage(storage);
  }
}

