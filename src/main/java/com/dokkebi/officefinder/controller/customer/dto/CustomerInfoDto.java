package com.dokkebi.officefinder.controller.customer.dto;

import com.dokkebi.officefinder.entity.Customer;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerInfoDto {
  private Long id;
  private String email;
  private String name;
  private long point;
  private Set<String> roles;
  private Set<PointChargeHistoryDto> histories;
  private String profileImagePath;

  public CustomerInfoDto(Long id, String email, String name, long point, Set<String> roles,
      Set<PointChargeHistoryDto> histories, String profileImagePath) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.point = point;
    this.roles = roles;
    this.histories = histories;
    this.profileImagePath = profileImagePath;
  }

  public static CustomerInfoDto from(Customer customer, Set<PointChargeHistoryDto> chargeHistory){
    return CustomerInfoDto.builder()
        .id(customer.getId())
        .email(customer.getEmail())
        .name(customer.getName())
        .point(customer.getPoint())
        .roles(customer.getRoles())
        .histories(chargeHistory)
        .profileImagePath(customer.getProfileImage())
        .build();
  }
}