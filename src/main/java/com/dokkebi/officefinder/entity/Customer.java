package com.dokkebi.officefinder.entity;

import com.dokkebi.officefinder.utils.Converter;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "customer_id", nullable = false)
  private Long id;

  @Column(name = "customer_name", nullable = false, length = 12)
  private String name;

  @Column(name = "customer_email", nullable = false, unique = true, length = 50)
  private String email;

  @Column(name = "customer_password", nullable = false, length = 100)
  private String password;

  @Column(name = "customer_point", nullable = false)
  private long point;

  @Column(name = "customer_roles", nullable = false)
  @Convert(converter = Converter.RoleConverter.class)
  private Set<String> roles;

  @Builder
  private Customer(String name, String email, String password, long point, Set<String> roles) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.point = point;
    this.roles = roles;
  }

  /*
    회원 비밀번호 변경 메서드
     */
  public void changePassword(String newPassword) {
    this.password = newPassword;
  }

  /*
  회원의 포인트 충전 메서드
   */
  public void chargePoint(long additionalPoint) {
    this.point += additionalPoint;
  }

  /*
  포인트 사용 메서드
   */
  public void usePoint(int requiredPoint) {
    if (this.point < requiredPoint) {
      throw new IllegalArgumentException("포인트가 부족합니다. 충전해 주세요");
    }
    this.point -= requiredPoint;
  }
}
