package com.dokkebi.officefinder.service.customer;

import static org.assertj.core.api.Assertions.assertThat;

import com.dokkebi.officefinder.controller.customer.dto.CustomerInfoDto;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.PointChargeHistory;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.history.ChargeHistoryRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CustomerServiceTest {

  @Autowired
  private CustomerService customerService;
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private ChargeHistoryRepository chargeHistoryRepository;

  @AfterEach
  void tearDown() {
    chargeHistoryRepository.deleteAllInBatch();
    customerRepository.deleteAllInBatch();
  }

  @DisplayName("회원의 포인트를 충전한다. 충전한 기록은 DB에 저장되고 충전한 량 만큼 회원의 보유 포인트가 증가한다.")
  @Test
  public void chargePointTest() throws Exception {
    // given
    Customer customer = createCustomer("customer", "test@test.com", "12345", 0L);
    Customer savedCustomer = customerRepository.save(customer);

    // when
    customerService.chargeCustomerPoint(10000L, "test@test.com");

    // then
    List<PointChargeHistory> result = chargeHistoryRepository.findAll();

    assertThat(savedCustomer.getPoint()).isEqualTo(10000L);
    assertThat(result).hasSize(1)
        .extracting("chargeAmount")
        .containsExactlyInAnyOrder(
            10000L
        );

  }

  @DisplayName("회원정보를 가져온다.")
  @Test
  public void getCustomerInfoTest() {
    //given
    Customer customer = createCustomer("test", "test@test.com", "12345", 0L);
    Customer savedCustomer = customerRepository.save(customer);
    //when
    CustomerInfoDto customerInfoDto = customerService.getCustomerInfo(savedCustomer.getId());
    //then
    assertThat(customerInfoDto.getName()).isEqualTo("test");
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