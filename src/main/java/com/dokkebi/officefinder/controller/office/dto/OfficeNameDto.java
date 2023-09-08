package com.dokkebi.officefinder.controller.office.dto;

import com.dokkebi.officefinder.entity.office.Office;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OfficeNameDto {

  private Long id;
  private String officeName;

  public static OfficeNameDto from(Office office){
    return OfficeNameDto.builder()
        .id(office.getId())
        .officeName(office.getName())
        .build();
  }
}
