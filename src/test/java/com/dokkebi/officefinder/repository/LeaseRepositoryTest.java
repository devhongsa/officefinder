package com.dokkebi.officefinder.repository;

import static com.dokkebi.officefinder.entity.type.LeaseStatus.ACCEPTED;
import static com.dokkebi.officefinder.entity.type.LeaseStatus.AWAIT;
import static com.dokkebi.officefinder.entity.type.LeaseStatus.EXPIRED;
import static com.dokkebi.officefinder.entity.type.LeaseStatus.PROCEEDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
public class LeaseRepositoryTest {

  @Autowired
  private LeaseRepository leaseRepository;
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private OfficeRepository officeRepository;

  @Test
  @DisplayName("leaseId로 lease인스턴스를 Office와 Customer정보와 join fetch해서 가져올 수 있다.")
  public void findByLeaseId() throws Exception {
    //given
    Customer customer = createCustomer("customer", "test@test.com", "1234", 1000);
    Customer savedCustomer = customerRepository.save(customer);

    Office office = createOffice("office1");
    Office savedOffice = officeRepository.save(office);

    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusDays(20);

    Lease lease = createLease(savedCustomer, savedOffice, 10000L, AWAIT, startDate,
        endDate);
    Lease savedLease = leaseRepository.save(lease);

    //when
    Lease foundLease = leaseRepository.findById(savedLease.getId())
        .orElseThrow(() -> new Exception("계약이 없습니다"));

    //then
    assertThat(savedLease)
        .extracting("price", "leaseStatus", "leaseStartDate", "leaseEndDate")
        .contains(
            10000L, AWAIT, startDate, endDate
        );
  }

  @Test
  @DisplayName("회원의 id로 회원이 임대했던 기록을 페이징으로 조회할 수 있다.")
  void paging() {
    // Given
    Customer customer = createCustomer("customer", "test@test.com", "1234", 1000);
    Customer customer2 = createCustomer("customer2", "test2@test.com", "1234", 1000);
    customerRepository.saveAll(List.of(customer, customer2));

    Office office = createOffice("office1");
    Office savedOffice = officeRepository.save(office);

    LocalDate startDate = LocalDate.now().minusDays(20);
    LocalDate endDate = startDate.plusDays(20);

    LocalDate startDate2 = LocalDate.now().minusDays(10);
    LocalDate endDate2 = startDate2.plusDays(20);

    LocalDate startDate3 = LocalDate.now();
    LocalDate endDate3 = startDate3.plusDays(20);

    Lease lease = createLease(customer, savedOffice, 10000L, EXPIRED, startDate,
        endDate);
    Lease lease2 = createLease(customer, savedOffice, 20000L, EXPIRED, startDate2,
        endDate2);
    Lease lease3 = createLease(customer, savedOffice, 30000L, ACCEPTED, startDate3,
        endDate3);
    Lease lease4 = createLease(customer2, savedOffice, 40000L, ACCEPTED, startDate3,
        endDate3);

    leaseRepository.saveAll(List.of(lease, lease2, lease3, lease4));

    PageRequest pageRequest = PageRequest.of(0, 5);

    // When
    Page<Lease> result = leaseRepository.findByCustomerId(customer.getId(), pageRequest);
    List<Lease> content = result.getContent();

    // Then
    assertThat(content).hasSize(3)
        .extracting("leaseStatus", "leaseStartDate", "leaseEndDate")
        .containsExactlyInAnyOrder(
            tuple(EXPIRED, startDate, endDate),
            tuple(EXPIRED, startDate2, endDate2),
            tuple(ACCEPTED, startDate3, endDate3)
        );

    assertThat(content)
        .extracting(Lease::getOffice)
        .extracting(Office::getName)
        .containsExactlyInAnyOrder(
            "office1",
            "office1",
            "office1"
        );
  }

  @Test
  @DisplayName("오피스 id와 LeaseStatus를 바탕으로 임대 요청을 페이징으로 조회할 수 있다.")
  void paging_leaseRequest(){
    // Given
    Customer customer = createCustomer("customer", "test@test.com", "1234", 1000);
    Customer customer2 = createCustomer("customer2", "test2@test.com", "1234", 1000);
    customerRepository.saveAll(List.of(customer, customer2));

    Office office = createOffice("office1");
    Office savedOffice = officeRepository.save(office);

    LocalDate startDate = LocalDate.now().minusDays(20);
    LocalDate endDate = startDate.plusDays(20);

    LocalDate startDate2 = LocalDate.now().minusDays(10);
    LocalDate endDate2 = startDate2.plusDays(20);

    LocalDate startDate3 = LocalDate.now();
    LocalDate endDate3 = startDate3.plusDays(20);

    Lease lease = createLease(customer, savedOffice, 10000L, AWAIT, startDate,
        endDate);
    Lease lease2 = createLease(customer, savedOffice, 20000L, AWAIT, startDate2,
        endDate2);
    Lease lease3 = createLease(customer, savedOffice, 30000L, ACCEPTED, startDate3,
        endDate3);
    Lease lease4 = createLease(customer2, savedOffice, 40000L, PROCEEDING, startDate3,
        endDate3);

    leaseRepository.saveAll(List.of(lease, lease2, lease3, lease4));

    PageRequest pageRequest = PageRequest.of(0, 5);

    // When
    Page<Lease> result = leaseRepository.findByOfficeIdAndLeaseStatus(savedOffice.getId(), AWAIT, pageRequest);
    List<Lease> content = result.getContent();

    // Then
    assertThat(content).hasSize(2)
        .extracting("leaseStatus", "leaseStartDate", "leaseEndDate")
        .containsExactlyInAnyOrder(
            tuple(AWAIT, startDate, endDate),
            tuple(AWAIT, startDate2, endDate2)
        );

    assertThat(content)
        .extracting(Lease::getOffice)
        .extracting(Office::getName)
        .containsExactlyInAnyOrder(
            "office1", "office1"
        );
  }

  private static Lease createLease(Customer savedCustomer, Office savedOffice, long price, LeaseStatus status,
      LocalDate startDate, LocalDate endDate) {

    return Lease.builder()
        .customer(savedCustomer)
        .office(savedOffice)
        .price(price)
        .leaseStatus(status)
        .leaseStartDate(startDate)
        .leaseEndDate(endDate)
        .build();
  }

  private static Office createOffice(String officeName) {

    return Office.builder()
        .name(officeName)
        .build();
  }

  private static Customer createCustomer(String name, String email, String password, long point) {
    return Customer.builder()
        .name(name)
        .email(email)
        .password(password)
        .roles(Set.of("ROLE_CUSTOMER"))
        .point(point)
        .build();
  }
}