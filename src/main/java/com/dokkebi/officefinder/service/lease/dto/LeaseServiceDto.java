package com.dokkebi.officefinder.service.lease.dto;

import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.LeaseOfficeRequest;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LeaseServiceDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class LeaseOfficeRequestDto {
    private String email;

    private Long officeId;

    private LocalDate startDate;

    private int months;

    private int customerCount;

    private boolean isMonthlyPay;

    public static LeaseOfficeRequestDto of(String email, Long officeId, LeaseOfficeRequest request){
      return LeaseOfficeRequestDto.builder()
          .email(email)
          .officeId(officeId)
          .startDate(LocalDate.parse(request.getStartDate()))
          .months(request.getMonths())
          .customerCount(request.getCustomerCount())
          .isMonthlyPay(request.getIsMonthlyPay())
          .build();
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class LeaseServiceResponse{
    private String customerEmail;

    private String officeName;

    private long price;

    private LeaseStatus leaseStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    public static LeaseServiceResponse of(Lease lease){
      return LeaseServiceResponse.builder()
          .customerEmail(lease.getCustomer().getEmail())
          .officeName(lease.getOffice().getName())
          .price(lease.getPrice())
          .leaseStatus(lease.getLeaseStatus())
          .startDate(lease.getLeaseStartDate())
          .endDate(lease.getLeaseEndDate())
          .build();
    }
  }
}
