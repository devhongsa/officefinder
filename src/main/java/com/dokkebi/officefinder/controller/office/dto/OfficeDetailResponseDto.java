package com.dokkebi.officefinder.controller.office.dto;

import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.utils.AddressConverter;
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
  private String remainOffices;
  private long leaseFee;
  private int maxCapacity;
  private OfficeOptionDto officeOptionDto;

  public static OfficeDetailResponseDto fromEntity(Office office, String remainOffices) {
    String address = AddressConverter.getAddress(office.getOfficeLocation());

    return OfficeDetailResponseDto.builder()
        .officeName(office.getName())
        .remainOffices(remainOffices)
        .leaseFee(office.getLeaseFee())
        .maxCapacity(office.getMaxCapacity())
        .address(address)
        .officeOptionDto(OfficeOptionDto.fromEntity(office.getOfficeCondition()))
        .build();
  }
}
