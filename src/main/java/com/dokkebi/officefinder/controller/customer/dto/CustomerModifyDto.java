package com.dokkebi.officefinder.controller.customer.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerModifyDto {

  @NotEmpty
  private String newName;
}
