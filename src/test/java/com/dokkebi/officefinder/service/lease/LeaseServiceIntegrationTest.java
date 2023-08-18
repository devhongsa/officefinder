package com.dokkebi.officefinder.service.lease;

import static org.assertj.core.api.Assertions.assertThat;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficeLocation;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.entity.type.Address;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.LeaseRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.repository.office.location.OfficeLocationRepository;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseLookUpServiceResponse;
import java.time.LocalDate;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
public class LeaseServiceIntegrationTest {

  @Autowired
  private OfficeLocationRepository officeLocationRepository;

  @Autowired
  private LeaseService leaseService;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private OfficeRepository officeRepository;

  @Autowired
  private LeaseRepository leaseRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  private Customer testCustomer;
  private Office testOffice1, testOffice2;

  @Test
  @DisplayName("임대 정보 정상 조회 테스트")
  public void lookupLeaseTest() {

    // Given
    initializeTestData();
    Pageable pageable = PageRequest.of(0, 10);

    // When
    Page<LeaseLookUpServiceResponse> result = leaseService.lookupLease(testCustomer.getEmail(), pageable);

    // Then
    assertThat(result).isNotEmpty().hasSize(2);
    LeaseLookUpServiceResponse firstResult = result.getContent().get(0);
    LeaseLookUpServiceResponse secondResult = result.getContent().get(1);

    // 첫번째 임대 결과는 리뷰가 있어야 함
    assertThat(firstResult.isReviewed()).isTrue();
    assertThat(secondResult.isReviewed()).isFalse();

    assertThat(firstResult.getName()).isEqualTo(testOffice1.getName());
    assertThat(secondResult.getName()).isEqualTo(testOffice2.getName());
  }

  private void initializeTestData() {
    // 테스트 데이터 설정
    testCustomer = createCustomer("TestCustomer", "test@Example.com", "", Set.of("a"), 0);
    testOffice1 = createOffice("TestOffice1");
    testOffice2 = createOffice("TestOffice2");
    Lease lease1 = createLease(testCustomer, testOffice1, LeaseStatus.AWAIT, LocalDate.now(),
        LocalDate.now().plusDays(10), false, 1000000);
    createLease(testCustomer, testOffice2, LeaseStatus.ACCEPTED, LocalDate.now().plusDays(5), LocalDate.now().plusDays(20), true, 1500000);

    // 첫 번째 임대에 대해 리뷰 생성
    createReview(testCustomer, testOffice1, lease1, 5, "Great place!");
  }

  private void createReview(Customer customer, Office office, Lease lease, int rate, String description) {
    reviewRepository.save(Review.builder()
        .customer(customer)
        .office(office)
        .lease(lease)
        .rate(rate)
        .description(description)
        .build());
  }

  private Customer createCustomer(String name, String email, String password, Set<String> roles, int point) {
    return customerRepository.save(Customer.builder()
        .name(name)
        .email(email)
        .password(password)
        .roles(roles)
        .point(point)
        .build());
  }

  private Office createOffice(String name) {

    Office office = Office.builder().name(name).build();
    officeRepository.save(office);

    Address address = Address.builder()
        .legion("TestLegion")
        .city("TestCity")
        .town("TestTown")
        .village("TestVillage")
        .bungi("TestBungi")
        .street("TestStreet")
        .buildingNumber("TestBuildingNumber")
        .zipcode(123)
        .build();

    OfficeLocation officeLocation = OfficeLocation.builder()
        .office(office)
        .address(address)
        .build();

    officeLocationRepository.save(officeLocation);

    office.setOfficeLocation(officeLocation);

    return office;
  }


  private Lease createLease(Customer customer, Office office, LeaseStatus status, LocalDate start, LocalDate end, boolean isMonthly, long price) {
    return leaseRepository.save(Lease.builder()
        .customer(customer)
        .office(office)
        .leaseStatus(status)
        .leaseStartDate(start)
        .leaseEndDate(end)
        .isMonthlyPay(isMonthly)
        .price(price)
        .build());
  }
}

