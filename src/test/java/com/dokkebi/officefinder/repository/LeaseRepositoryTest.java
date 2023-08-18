package com.dokkebi.officefinder.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    Customer customer = customerRepository.save(Customer.builder().name("1").email("").password("").roles(
        Set.of("a")).point(0).build());
    Office office = officeRepository.save(Office.builder().name("1").build());

    Lease lease = Lease.builder()
        .customer(customer)
        .office(office)
        .build();
    Lease savedLease = leaseRepository.save(lease);
      //when
    Lease foundLease = leaseRepository.findById(savedLease.getId()).orElseThrow(()->new Exception("계약이 없습니다"));
      //then
    assertEquals(lease.getId(), foundLease.getId());
    assertEquals(lease.getCustomer(), foundLease.getCustomer());
    assertEquals(lease.getOffice(), foundLease.getOffice());
  }

  @Test
  @DisplayName("페이징 처리 테스트")
  void paging(){
    //Given
    Customer customer = customerRepository.save(Customer.builder().name("1").email("").password("")
        .roles(Set.of("a")).point(0).build());

    Office office = officeRepository.save(Office.builder().name("1").build());

    IntStream.range(0,100).forEach(i -> {
      Lease lease = Lease.builder()
          .customer(customer)
          .office(office)
          .price(1000L)
          .leaseStatus(LeaseStatus.AWAIT)
          .leaseStartDate(LocalDate.now())
          .leaseEndDate(LocalDate.now().plusDays(10))
          .isMonthlyPay(false)
          .build();

      leaseRepository.save(lease);
    });

    // When
    int page = 3;
    int size = 10;
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Lease> leasePage = leaseRepository.findByCustomerId(customer.getId(), pageable);

    // Then
    assertEquals(size, leasePage.getContent().size());
    assertEquals(100, leasePage.getTotalElements());
  }
}