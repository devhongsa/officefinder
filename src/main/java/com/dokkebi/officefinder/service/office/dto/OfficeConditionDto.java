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
public class OfficeConditionDto {

  boolean haveAirCondition;
  boolean haveCafe;
  boolean havePrinter;
  boolean packageSendServiceAvailable;
  boolean haveDoorLock;
  boolean faxServiceAvailable;
  boolean havePublicKitchen;
  boolean havePublicLounge;
  boolean havePrivateLocker;
  boolean haveTvProjector;
  boolean haveWhiteBoard;
  boolean haveWifi;
  boolean haveShowerBooth;
  boolean haveStorage;
  boolean haveHeater;

  public static OfficeConditionDto fromRequest(OfficeCreateRequestDto request) {
    return OfficeConditionDto.builder()
        .haveAirCondition(request.getHaveAirCondition())
        .haveCafe(request.getHaveCafe())
        .havePrinter(request.getHavePrinter())
        .packageSendServiceAvailable(request.getPackageSendServiceAvailable())
        .haveDoorLock(request.getHaveDoorLock())
        .faxServiceAvailable(request.getFaxServiceAvailable())
        .havePublicKitchen(request.getHavePublicKitchen())
        .havePublicLounge(request.getHavePublicLounge())
        .havePrivateLocker(request.getHavePrivateLocker())
        .haveTvProjector(request.getHaveTvProjector())
        .haveWhiteBoard(request.getHaveWhiteBoard())
        .haveStorage(request.getHaveStorage())
        .haveHeater(request.getHaveHeater())
        .haveWifi(request.getHaveWifi())
        .haveShowerBooth(request.getHaveShowerBooth())
        .build();
  }

  public static OfficeConditionDto fromRequest(OfficeModifyRequestDto request) {
    return OfficeConditionDto.builder()
        .haveAirCondition(request.getHaveAirCondition())
        .haveCafe(request.getHaveCafe())
        .havePrinter(request.getHavePrinter())
        .packageSendServiceAvailable(request.getPackageSendServiceAvailable())
        .haveDoorLock(request.getHaveDoorLock())
        .faxServiceAvailable(request.getFaxServiceAvailable())
        .havePublicKitchen(request.getHavePublicKitchen())
        .havePublicLounge(request.getHavePublicLounge())
        .havePrivateLocker(request.getHavePrivateLocker())
        .haveTvProjector(request.getHaveTvProjector())
        .haveWhiteBoard(request.getHaveWhiteBoard())
        .haveStorage(request.getHaveStorage())
        .haveHeater(request.getHaveHeater())
        .haveWifi(request.getHaveWifi())
        .haveShowerBooth(request.getHaveShowerBooth())
        .build();
  }
}
