package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
    Assertions.assertEquals(lease.getId(), foundLease.getId());
    Assertions.assertEquals(lease.getCustomer(), foundLease.getCustomer());
    Assertions.assertEquals(lease.getOffice(), foundLease.getOffice());
  }
}