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

  String legion;
  String city;
  String town;
  String village;
  String bungi;
  String street;
  String buildingNumber;
  int zipcode;
  double latitude;
  double longitude;

  public static OfficeLocationDto fromRequest(OfficeCreateRequestDto request) {
    return OfficeLocationDto.builder()
        .legion(request.getLegion())
        .city(request.getCity())
        .town(request.getTown())
        .village(request.getVillage())
        .street(request.getStreet())
        .zipcode(request.getZipcode())
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .build();
  }

  public static OfficeLocationDto fromRequest(OfficeModifyRequestDto request) {
    return OfficeLocationDto.builder()
        .legion(request.getLegion())
        .city(request.getCity())
        .town(request.getTown())
        .village(request.getVillage())
        .street(request.getStreet())
        .zipcode(request.getZipcode())
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .build();
  }
}
