package com.dokkebi.officefinder.controller.review.dto;

import com.dokkebi.officefinder.service.review.dto.ReviewServiceDto.SubmitServiceResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReviewControllerDto {

  @Getter
  @NoArgsConstructor
  public static class SubmitControllerRequest {
    private int rate;
    private String description;

    @Builder
    public SubmitControllerRequest(int rate, String description) {
      this.rate = rate;
      this.description = description;
    }
  }

  @Getter
  @NoArgsConstructor
  public static class SubmitControllerResponse {
    private String customerName;
    private String officeName;

    @Builder
    public SubmitControllerResponse(String customerName, String officeName) {
      this.customerName = customerName;
      this.officeName = officeName;
    }

    public SubmitControllerResponse from(SubmitServiceResponse submitServiceResponse) {
      return SubmitControllerResponse.builder()
          .customerName(submitServiceResponse.getCustomerName())
          .officeName(submitServiceResponse.getOfficeName())
          .build();
    }
  }

}