package com.dokkebi.officefinder.controller.customer.dto;

import com.dokkebi.officefinder.entity.Customer;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CustomerOverViewInfoDto {

  private Long id;
  private String name;
  private long point;
  private Set<String> roles;
  private String profileImagePath;

  public static CustomerOverViewInfoDto from(Customer customer){
    return CustomerOverViewInfoDto.builder()
        .id(customer.getId())
        .name(customer.getName())
        .point(customer.getPoint())
        .roles(customer.getRoles())
        .profileImagePath(customer.getProfileImage())
        .build();
  }
}
