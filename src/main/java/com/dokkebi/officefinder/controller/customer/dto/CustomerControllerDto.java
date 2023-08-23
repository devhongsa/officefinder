package com.dokkebi.officefinder.controller.customer.dto;

import com.dokkebi.officefinder.entity.PointChargeHistory;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

public class CustomerControllerDto {

  @Getter
  @Builder
  public static class CustomerInfo {
    private Long id;
    private String email;
    private String name;
    private long point;
    private Set<String> roles;
    private Page<PointChargeHistory> pointChargeHistories;

    public CustomerInfo(Long id, String email, String name, long point, Set<String> roles,
        Page<PointChargeHistory> pointChargeHistories) {
      this.id = id;
      this.email = email;
      this.name = name;
      this.point = point;
      this.roles = roles;
      this.pointChargeHistories = pointChargeHistories;
    }
  }

}