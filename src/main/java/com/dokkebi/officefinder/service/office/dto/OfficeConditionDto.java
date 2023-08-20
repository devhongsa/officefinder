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
        .haveAirCondition(request.getOfficeOption().getHaveAirCondition())
        .haveCafe(request.getOfficeOption().getHaveCafe())
        .havePrinter(request.getOfficeOption().getHavePrinter())
        .packageSendServiceAvailable(request.getOfficeOption().getPackageSendServiceAvailable())
        .haveDoorLock(request.getOfficeOption().getHaveDoorLock())
        .faxServiceAvailable(request.getOfficeOption().getFaxServiceAvailable())
        .havePublicKitchen(request.getOfficeOption().getHavePublicKitchen())
        .havePublicLounge(request.getOfficeOption().getHavePublicLounge())
        .havePrivateLocker(request.getOfficeOption().getHavePrivateLocker())
        .haveTvProjector(request.getOfficeOption().getHaveTvProjector())
        .haveWhiteBoard(request.getOfficeOption().getHaveWhiteBoard())
        .haveStorage(request.getOfficeOption().getHaveStorage())
        .haveHeater(request.getOfficeOption().getHaveHeater())
        .haveWifi(request.getOfficeOption().getHaveWifi())
        .haveShowerBooth(request.getOfficeOption().getHaveShowerBooth())
        .build();
  }

  public static OfficeConditionDto fromRequest(OfficeModifyRequestDto request) {
    return OfficeConditionDto.builder()
        .haveAirCondition(request.getOfficeOption().getHaveAirCondition())
        .haveCafe(request.getOfficeOption().getHaveCafe())
        .havePrinter(request.getOfficeOption().getHavePrinter())
        .packageSendServiceAvailable(request.getOfficeOption().getPackageSendServiceAvailable())
        .haveDoorLock(request.getOfficeOption().getHaveDoorLock())
        .faxServiceAvailable(request.getOfficeOption().getFaxServiceAvailable())
        .havePublicKitchen(request.getOfficeOption().getHavePublicKitchen())
        .havePublicLounge(request.getOfficeOption().getHavePublicLounge())
        .havePrivateLocker(request.getOfficeOption().getHavePrivateLocker())
        .haveTvProjector(request.getOfficeOption().getHaveTvProjector())
        .haveWhiteBoard(request.getOfficeOption().getHaveWhiteBoard())
        .haveStorage(request.getOfficeOption().getHaveStorage())
        .haveHeater(request.getOfficeOption().getHaveHeater())
        .haveWifi(request.getOfficeOption().getHaveWifi())
        .haveShowerBooth(request.getOfficeOption().getHaveShowerBooth())
        .build();
  }
}
