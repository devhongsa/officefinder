package com.dokkebi.officefinder.controller.customer.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PointChargeRequestDto {

  @NotNull
  private Long chargeAmount;
}
