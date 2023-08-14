package com.dokkebi.officefinder.entity.lease;

import static org.assertj.core.api.Assertions.assertThat;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.entity.type.UserRole;
import java.time.LocalDate;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LeaseTest {

  @Autowired
  private EntityManager entityManager;

  @DisplayName("임대 계약 상태를 변경할 수 있다.")
  @Test
  public void changeLeaseStatusTest() throws Exception {
    // given
    Customer customer = createCustomer("kim", "test@test.com", "12345", 0, UserRole.CUSTOMER);
    Office office = createOffice("office1", 50000, 5);

    Lease lease = createLease(customer, office, 1500000, LeaseStatus.AWAIT, LocalDate.now(),
        LocalDate.now().plusDays(30));

    entityManager.persist(lease);

    // when
    Lease leaseData = entityManager.find(Lease.class, lease.getId());
    leaseData.changeLeaseStatus(LeaseStatus.PROCEEDING);

    // then
    assertThat(leaseData.getLeaseStatus()).isEqualTo(LeaseStatus.PROCEEDING);
  }

  private Office createOffice(String officeName, long leaseFee, int maxCapacity) {
    return Office.builder()
        .name(officeName)
        .leaseFee(leaseFee)
        .maxCapacity(maxCapacity)
        .build();
  }

  private Customer createCustomer(String name, String email, String password, long point,
      UserRole userRole) {
    return Customer.builder()
        .name(name)
        .email(email)
        .password(password)
        .point(point)
        .role(userRole)
        .build();
  }

  private Lease createLease(Customer customer, Office office, long price,
      LeaseStatus leaseStatus, LocalDate leaseStartDate, LocalDate leaseEndDate) {
    return Lease.builder()
        .customer(customer)
        .office(office)
        .price(price)
        .leaseStatus(leaseStatus)
        .leaseStartDate(leaseStartDate)
        .leaseEndDate(leaseEndDate)
        .build();
  }

}