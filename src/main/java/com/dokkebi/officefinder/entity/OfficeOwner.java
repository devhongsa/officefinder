package com.dokkebi.officefinder.entity;

import com.dokkebi.officefinder.utils.Converter;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class OfficeOwner extends BaseEntity implements UserDetails {

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
  private OfficeOwner(Long id, String name, String email, String password, String businessNumber, long point, Set<String> roles) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.businessNumber = businessNumber;
    this.point = point;
    this.roles = roles;
  }

  public void changePassword(String newPassword) {
    this.password = newPassword;
  }

  public void addPoint(int additionalPoint) {
    this.point += additionalPoint;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }


}
