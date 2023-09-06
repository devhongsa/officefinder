package com.dokkebi.officefinder.controller.officeowner.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OfficeOwnerModifyDto {

  @NotEmpty
  private String name;
}
