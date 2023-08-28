package com.dokkebi.officefinder.service.officeowner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OfficeOwnerServiceDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RentalStatusDto {
    private int officeRoomCount;
    private int roomsInUse;
    private double leaseRate;
  }

}
