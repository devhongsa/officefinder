package com.dokkebi.officefinder.controller.customer.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointChargeHistoryDto {

  private long chargeAmount;
  private LocalDateTime createdAt;

}