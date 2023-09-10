package com.dokkebi.officefinder.controller.customer.dto;

import com.dokkebi.officefinder.entity.PointChargeHistory;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointChargeHistoryDto {

  private long chargeAmount;
  private LocalDateTime createdAt;

  public static PointChargeHistoryDto from(PointChargeHistory history){
    return PointChargeHistoryDto.builder()
        .chargeAmount(history.getChargeAmount())
        .createdAt(history.getCreatedAt())
        .build();
  }
}