package com.dokkebi.officefinder.controller.officeowner.dto;

import com.dokkebi.officefinder.entity.office.Office;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OwnerOfficeOverViewDto {

  private String officeName;
  private String address;

  public static OwnerOfficeOverViewDto fromEntity(Office office) {
    return OwnerOfficeOverViewDto.builder()
        .officeName(office.getName())
        .address(office.getOfficeAddress())
        .build();
  }
}
