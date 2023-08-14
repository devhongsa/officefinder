package com.dokkebi.officefinder.entity;

import com.dokkebi.officefinder.utils.Converter;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
  @Column(name = "owner_id")
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

  @Column(name = "owner_roles", nullable = false)
  @Convert(converter = Converter.RoleConverter.class)
  private Set<String> roles;

  @Builder
  private OfficeOwner(String name, String email, String password, String businessNumber) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.businessNumber = businessNumber;
    this.point = 0;
    this.roles = Set.of("ROLE_AGENT");
  }

  public void changePassword(String newPassword) {
    this.password = newPassword;
  }

  public void addPoint(int additionalPoint) {
    this.point += additionalPoint;
  }
}
