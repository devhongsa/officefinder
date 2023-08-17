package com.dokkebi.officefinder.service.review.dto;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.SubmitControllerRequest;
import com.dokkebi.officefinder.entity.review.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReviewServiceDto {

  @Getter
  @NoArgsConstructor
  public static class SubmitServiceRequest {
    private String customerEmail;
    private Long leaseId;
    private int rate;
    private String description;

    @Builder
    public SubmitServiceRequest(String customerEmail, Long leaseId, int rate, String description) {
      this.customerEmail = customerEmail;
      this.leaseId = leaseId;
      this.rate = rate;
      this.description = description;
    }

    public SubmitServiceRequest from(SubmitControllerRequest submitControllerRequest, String customerEmail, Long leaseId) {
      return SubmitServiceRequest.builder()
          .customerEmail(customerEmail)
          .leaseId(leaseId)
          .rate(submitControllerRequest.getRate())
          .description(submitControllerRequest.getDescription())
          .build();
    }
  }

  @Getter
  @NoArgsConstructor
  public static class SubmitServiceResponse {
    private String customerName;
    private String officeName;

    @Builder
    public SubmitServiceResponse(String customerName, String officeName) {
      this.customerName = customerName;
      this.officeName = officeName;
    }

    public SubmitServiceResponse from(Review review) {
      return SubmitServiceResponse.builder()
          .customerName(review.getCustomer().getName())
          .officeName(review.getOffice().getName())
          .build();
    }

  }

}