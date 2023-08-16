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
public class OfficeModifyRequestDto {

  @NotEmpty
  String officeName;
  @NotNull
  Integer maxCapacity;
  @NotNull
  Long leaseFee;
  @NotNull
  Integer remainRoom;

  // address
  @NotEmpty
  String legion;
  @NotEmpty
  String city;
  @NotEmpty
  String town;
  @NotNull
  String village;
  @NotEmpty
  String bungi;
  @NotEmpty
  String street;
  @NotEmpty
  String buildingNumber;
  @NotNull
  Integer zipcode;
  @NotNull
  Double latitude;
  @NotNull
  Double longitude;

  // option
  @NotNull
  Boolean haveAirCondition;
  @NotNull
  Boolean haveCafe;
  @NotNull
  Boolean havePrinter;
  @NotNull
  Boolean packageSendServiceAvailable;
  @NotNull
  Boolean haveDoorLock;
  @NotNull
  Boolean faxServiceAvailable;
  @NotNull
  Boolean havePublicKitchen;
  @NotNull
  Boolean havePublicLounge;
  @NotNull
  Boolean havePrivateLocker;
  @NotNull
  Boolean haveTvProjector;
  @NotNull
  Boolean haveWhiteBoard;
  @NotNull
  Boolean haveWifi;
  @NotNull
  Boolean haveShowerBooth;
  @NotNull
  Boolean haveStorage;
  @NotNull
  Boolean haveHeater;
}
