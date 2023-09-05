package com.dokkebi.officefinder.controller.office.dto;

import com.dokkebi.officefinder.entity.office.OfficeCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OfficeOptionDto {

  private boolean haveAirCondition;
  private boolean haveCafe;
  private boolean havePrinter;
  private boolean packageSendServiceAvailable;
  private boolean haveDoorLock;
  private boolean faxServiceAvailable;
  private boolean havePublicKitchen;
  private boolean havePublicLounge;
  private boolean havePrivateLocker;
  private boolean haveTvProjector;
  private boolean haveWhiteBoard;
  private boolean haveWifi;
  private boolean haveShowerBooth;
  private boolean haveStorage;
  private boolean haveHeater;
  private boolean haveParkArea;

  public static OfficeOptionDto fromEntity(OfficeCondition officeCondition) {
    return OfficeOptionDto.builder()
        .haveAirCondition(officeCondition.isAirCondition())
        .haveCafe(officeCondition.isCafe())
        .havePrinter(officeCondition.isPrinter())
        .packageSendServiceAvailable(officeCondition.isPackageSendService())
        .haveDoorLock(officeCondition.isDoorLock())
        .faxServiceAvailable(officeCondition.isFax())
        .havePublicKitchen(officeCondition.isPublicKitchen())
        .havePublicLounge(officeCondition.isPublicLounge())
        .havePrivateLocker(officeCondition.isPrivateLocker())
        .haveTvProjector(officeCondition.isTvProjector())
        .haveWhiteBoard(officeCondition.isWhiteboard())
        .haveWifi(officeCondition.isWifi())
        .haveShowerBooth(officeCondition.isShowerBooth())
        .haveStorage(officeCondition.isStorage())
        .haveHeater(officeCondition.isHeaterCondition())
        .haveParkArea(officeCondition.isParkArea())
        .build();
  }
}
