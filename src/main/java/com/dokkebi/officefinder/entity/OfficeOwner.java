package com.dokkebi.officefinder.entity;

import com.dokkebi.officefinder.entity.type.UserRole;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class OfficeOwner extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "onwer_id")
  private Long id;

  @Column(name = "owner_name", nullable = false, length = 12)
  private String name;

  @Column(name = "owner_email", nullable = false, length = 50)
  private String email;

  @Column(name = "owner_password", nullable = false)
  private String password;

  @Column(name = "owner_business_number", nullable = false)
  private String businessNumber;

  @Column(name = "owner_point", nullable = false)
  private long point;

  @Column(name = "owner_role", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserRole role;

  @Builder
  private OfficeOwner(Long id, String name, String email, String password, String businessNumber,
      long point, UserRole role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.businessNumber = businessNumber;
    this.point = point;
    this.role = role;
  }

  public void changePassword(String newPassword) {
    this.password = newPassword;
  }

  public void addPoint(int additionalPoint) {
    this.point += additionalPoint;
  }
}
