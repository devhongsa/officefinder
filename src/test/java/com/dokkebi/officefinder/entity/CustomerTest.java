package com.dokkebi.officefinder.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dokkebi.officefinder.entity.type.UserRole;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CustomerTest {

  @Autowired
  private EntityManager entityManager;

  @DisplayName("회원의 비밀번호를 변경할 수 있다.")
  @Test
  public void changePasswordTest() {
    // given
    Customer customer = createCustomer("customer", "test1@naver.com", "1234", 0, UserRole.CUSTOMER);
    entityManager.persist(customer);

    entityManager.flush();
    entityManager.clear();

    // when
    Customer savedCustomer = entityManager.find(Customer.class, customer.getId());
    savedCustomer.changePassword("5678");

    entityManager.flush();
    entityManager.clear();

    // then
    Customer result = entityManager.find(Customer.class, customer.getId());
    assertThat(result.getPassword()).isEqualTo("5678");
  }

  @DisplayName("회원의 포인트를 증가시킬 수 있다.")
  @Test
  public void chargePointTest() {
    // given
    Customer customer = createCustomer("test1", "test2@naver.com", "5678", 0, UserRole.CUSTOMER);
    entityManager.persist(customer);

    entityManager.flush();
    entityManager.clear();

    // when
    Customer savedCustomer = entityManager.find(Customer.class, customer.getId());
    savedCustomer.chargePoint(100000L);

    entityManager.flush();
    entityManager.clear();

    // then
    Customer result = entityManager.find(Customer.class, customer.getId());
    assertThat(result.getPoint()).isEqualTo(100000L);
  }

  @DisplayName("회원의 포인트를 감소시킬 수 있다.")
  @Test
  public void usePointTest() {
    // given
    Customer customer = createCustomer("test1", "test2@naver.com", "5678", 150000,
        UserRole.CUSTOMER);
    entityManager.persist(customer);

    entityManager.flush();
    entityManager.clear();

    // when
    Customer savedCustomer = entityManager.find(Customer.class, customer.getId());
    savedCustomer.usePoint(100000);

    entityManager.flush();
    entityManager.clear();

    // then
    Customer result = entityManager.find(Customer.class, customer.getId());
    assertThat(result.getPoint()).isEqualTo(50000L);
  }

  @DisplayName("회원이 보유 중인 포인트보다 더 많은 양을 사용해야 하면 예외를 발생시킨다.")
  @Test
  public void usePointTestWithNotEnoughPoint() {
    // given
    Customer customer = createCustomer("test1", "test2@naver.com", "5678", 150000,
        UserRole.CUSTOMER);
    entityManager.persist(customer);

    entityManager.flush();
    entityManager.clear();

    // when
    // then
    Customer savedCustomer = entityManager.find(Customer.class, customer.getId());
    assertThatThrownBy(() -> savedCustomer.usePoint(300000))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("포인트가 부족합니다. 충전해 주세요");
  }

  private static Customer createCustomer(String name, String email, String password, long point,
      UserRole userRole) {
    return Customer.builder()
        .name(name)
        .email(email)
        .password(password)
        .point(point)
        .role(userRole)
        .build();
  }
}