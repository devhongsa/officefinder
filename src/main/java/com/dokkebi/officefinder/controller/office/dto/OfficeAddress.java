package com.dokkebi.officefinder.controller.office.dto;

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
public class OfficeAddress {

  private String legion;
  private String city;
  private String town;
  private String village;
  private String street;
  private String zipcode;
}
