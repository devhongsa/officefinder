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
  private String imagePath;

  public static OwnerOfficeOverViewDto fromEntity(Office office, String imagePath) {
    return OwnerOfficeOverViewDto.builder()
        .officeName(office.getName())
        .address(office.getOfficeAddress())
        .imagePath(imagePath)
        .build();
  }
}
