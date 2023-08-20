package com.dokkebi.officefinder.service.office.dto;

import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeModifyRequestDto;
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
public class OfficeLocationDto {

  private String legion;
  private String city;
  private String town;
  private String village;
  private int zipcode;

  public static OfficeLocationDto fromRequest(OfficeCreateRequestDto request) {
    return OfficeLocationDto.builder()
        .legion(request.getAddress().getLegion())
        .city(request.getAddress().getCity())
        .town(request.getAddress().getTown())
        .village(request.getAddress().getVillage())
        .zipcode(Integer.parseInt(request.getAddress().getZipcode()))
        .build();
  }

  public static OfficeLocationDto fromRequest(OfficeModifyRequestDto request) {
    return OfficeLocationDto.builder()
        .legion(request.getAddress().getLegion())
        .city(request.getAddress().getCity())
        .town(request.getAddress().getTown())
        .village(request.getAddress().getVillage())
        .zipcode(Integer.parseInt(request.getAddress().getZipcode()))
        .build();
  }
}
