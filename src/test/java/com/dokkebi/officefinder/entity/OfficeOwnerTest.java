package com.dokkebi.officefinder.entity;


import java.util.Set;
import javax.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class OfficeOwnerTest {

  @Autowired
  private EntityManager entityManager;

  @DisplayName("임대주의 보유 포인트를 증가시킬 수 있다.")
  @Test
  public void addPointTest() {
    // given
    OfficeOwner owner = createOwner("owner1", "owner1@test.com", "1234", "1234-5678", 0,
        Set.of("ROLE_OFFICE_OWNER"));

    entityManager.persist(owner);
    entityManager.flush();
    entityManager.clear();

    // when
    OfficeOwner officeOwner = entityManager.find(OfficeOwner.class, owner.getId());
    officeOwner.addPoint(10000);

    entityManager.flush();
    entityManager.clear();

    // then
    OfficeOwner result = entityManager.find(OfficeOwner.class, owner.getId());
    Assertions.assertThat(result.getPoint()).isEqualTo(10000);
  }

  @DisplayName("임대주의 패스워드를 변경할 수 있다.")
  @Test
  public void changePasswordTest() {
    // given
    OfficeOwner owner = createOwner("owner1", "owner1@test.com", "1234", "1234-5678", 0,
        Set.of("ROLE_OFFICE_OWNER"));

    entityManager.persist(owner);
    entityManager.flush();
    entityManager.clear();

    // when
    OfficeOwner officeOwner = entityManager.find(OfficeOwner.class, owner.getId());
    officeOwner.changePassword("5678");

    entityManager.flush();
    entityManager.clear();

    // then
    OfficeOwner result = entityManager.find(OfficeOwner.class, owner.getId());
    Assertions.assertThat(result.getPassword()).isEqualTo("5678");
  }

  private static OfficeOwner createOwner(String name, String email, String password,
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


}