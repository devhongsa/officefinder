package com.dokkebi.officefinder.controller.office.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficeCreateRequestDto {

  @NotEmpty
  private String officeName;
  @NotNull
  private Integer maxCapacity;
  @NotNull
  private Long leaseFee;
  @NotNull
  private Integer remainRoom;

  // address
  private OfficeAddress address;
  private OfficeOption officeOption;
}
