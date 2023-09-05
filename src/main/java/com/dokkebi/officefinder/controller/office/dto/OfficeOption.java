package com.dokkebi.officefinder.controller.office.dto;

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
public class OfficeOption {

  @NotNull
  private Boolean haveAirCondition;
  @NotNull
  private Boolean haveCafe;
  @NotNull
  private Boolean havePrinter;
  @NotNull
  private Boolean packageSendServiceAvailable;
  @NotNull
  private Boolean haveDoorLock;
  @NotNull
  private Boolean faxServiceAvailable;
  @NotNull
  private Boolean havePublicKitchen;
  @NotNull
  private Boolean havePublicLounge;
  @NotNull
  private Boolean havePrivateLocker;
  @NotNull
  private Boolean haveTvProjector;
  @NotNull
  private Boolean haveWhiteBoard;
  @NotNull
  private Boolean haveWifi;
  @NotNull
  private Boolean haveShowerBooth;
  @NotNull
  private Boolean haveStorage;
  @NotNull
  private Boolean haveHeater;
  @NotNull
  private Boolean haveParkArea;

}
