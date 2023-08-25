package com.dokkebi.officefinder.controller.customer.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;

public class CustomerControllerDto {

  @Getter
  @Builder
  public static class CustomerInfo {
    private Long id;
    private String email;
    private String name;
    private long point;
    private Set<String> roles;
    private Set<PointChargeHistoryDto> histories;

    public CustomerInfo(Long id, String email, String name, long point, Set<String> roles,
        Set<PointChargeHistoryDto> histories) {
      this.id = id;
      this.email = email;
      this.name = name;
      this.point = point;
      this.roles = roles;
      this.histories = histories;
    }
  }

}