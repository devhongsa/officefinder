package com.dokkebi.officefinder.controller.office.dto;

import com.dokkebi.officefinder.entity.office.Office;
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
public class OfficeDetailResponseDto {

  private String officeName;
  private String address;
  private int remainOffices;
  private long leaseFee;
  private int maxCapacity;
  private OfficeOptionDto officeOptionDto;

  public static OfficeDetailResponseDto fromEntity(Office office) {
    return OfficeDetailResponseDto.builder()
        .officeName(office.getName())
        .leaseFee(office.getLeaseFee())
        .maxCapacity(office.getMaxCapacity())
        .address(office.getOfficeAddress())
        .remainOffices(office.getMaxRoomCount())
        .officeOptionDto(OfficeOptionDto.fromEntity(office.getOfficeCondition()))
        .build();
  }
}
