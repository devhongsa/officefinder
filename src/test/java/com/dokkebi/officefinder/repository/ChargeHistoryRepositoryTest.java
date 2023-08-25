package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.PointChargeHistory;
import com.dokkebi.officefinder.repository.history.ChargeHistoryRepository;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ChargeHistoryRepositoryTest {

  @Autowired
  private ChargeHistoryRepository chargeHistoryRepository;
  @Autowired
  private CustomerRepository customerRepository;

  @DisplayName("충전내역을 상위 10개만 가져올 수 있다.")
  @Test
  void getTop10() {
      //given
    Customer customer = customerRepository.save(
        createCustomer("test", "test@test.com", "tst", 0)
    );
      //when
    for (int i = 1; i < 12; i++) {
      chargeHistoryRepository.save(PointChargeHistory.builder()
          .customer(customer).chargeAmount(i).build());
    }
    Set<PointChargeHistory> set = chargeHistoryRepository.findTop10ByCustomerIdOrderByCreatedAtDesc(
        customer.getId());
    //then
    Assertions.assertThat(set.size()).isEqualTo(10);
  }

  private Customer createCustomer(String name, String email, String password, long point) {
    return Customer.builder()
        .name(name)
        .email(email)
        .password(password)
        .roles(Set.of("ROLE_CUSTOMER"))
        .point(point)
        .build();
  }
}