package com.dokkebi.officefinder.controller.lease.dto;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseLookUpServiceResponse;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeServiceResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

public class LeaseControllerDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class LeaseOfficeRequest {
    @NotBlank(message = "임대 시작일을 입력해 주세요.")
    private String startDate;

    @NotNull(message = "사용할 개월 수는 필수로 입력해야 합니다." )
    @Range(min = 1, max = 11, message = "사용할 개월 수는 1에서 11개월 사이이어야 합니다.")
    private Integer months;

    @NotNull(message = "사용할 인원 수는 필수로 입력해야 합니다.")
    @Min(value = 1, message = "사용할 인원은 최소 1명 이상이어야 합니다.")
    private Integer customerCount;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class LeaseSuccessResponse {
    private String customerEmail;

    private String officeName;

    private long price;

    private LocalDate startDate;

    private LocalDate endDate;

    public static LeaseSuccessResponse of(LeaseOfficeServiceResponse resp){
      return LeaseSuccessResponse.builder()
          .customerEmail(resp.getCustomerEmail())
          .officeName(resp.getOfficeName())
          .price(resp.getPrice())
          .startDate(resp.getStartDate())
          .endDate(resp.getEndDate())
          .build();
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class LeaseLookUpResponse{
    private Long leaseId;

    private String name;

    private String location;

    private LeaseStatus leaseStatus;

    private LocalDate paymentDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isMonthly;

    private boolean isReviewed;

    public static LeaseLookUpResponse of (LeaseLookUpServiceResponse resp){
      return LeaseLookUpResponse.builder()
          .leaseId(resp.getLeaseId())
          .name(resp.getName())
          .location(resp.getLocation())
          .leaseStatus(resp.getLeaseStatus())
          .paymentDate(resp.getPaymentDate())
          .startDate(resp.getStartDate())
          .endDate(resp.getEndDate())
          .isMonthly(resp.isMonthlyPay())
          .isReviewed(resp.isReviewed())
          .build();
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AgentLeaseLookUpResponse{

    private Long leaseId;

    private String customerName;

    private String customerEmail;

    private String officeName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long price;

    private LocalDateTime requestDateTime;

    public static AgentLeaseLookUpResponse of(Lease lease, String officeName){
      return AgentLeaseLookUpResponse.builder()
          .leaseId(lease.getId())
          .customerName(lease.getCustomer().getName())
          .customerEmail(lease.getCustomer().getEmail())
          .officeName(officeName)
          .startDate(lease.getLeaseStartDate())
          .endDate(lease.getLeaseEndDate())
          .price(lease.getPrice())
          .requestDateTime(lease.getCreatedAt())
          .build();
    }
  }
}
