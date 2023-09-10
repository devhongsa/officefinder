package com.dokkebi.officefinder.controller.officeowner.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfficeOwnerModifyDto {

  @NotEmpty
  private String name;
}
