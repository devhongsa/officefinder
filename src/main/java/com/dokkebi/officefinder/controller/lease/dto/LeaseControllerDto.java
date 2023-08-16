package com.dokkebi.officefinder.controller.lease.dto;

import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseServiceResponse;
import java.time.LocalDate;
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

    @NotNull(message = "월별 자동 결제 여부를 선택해 주세요.")
    private Boolean isMonthlyPay;
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

    public static LeaseSuccessResponse of(LeaseServiceResponse resp){
      return LeaseSuccessResponse.builder()
          .customerEmail(resp.getCustomerEmail())
          .officeName(resp.getOfficeName())
          .price(resp.getPrice())
          .startDate(resp.getStartDate())
          .endDate(resp.getEndDate())
          .build();
    }
  }
}
