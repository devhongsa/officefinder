package com.dokkebi.officefinder.controller.officeowner.dto;

import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.utils.AddressConverter;
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
    String address = AddressConverter.getAddress(office.getOfficeLocation());

    return OwnerOfficeOverViewDto.builder()
        .officeName(office.getName())
        .address(address)
        .build();
  }
}
