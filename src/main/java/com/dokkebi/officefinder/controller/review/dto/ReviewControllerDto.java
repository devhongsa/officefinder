package com.dokkebi.officefinder.controller.review.dto;

import com.dokkebi.officefinder.entity.review.Review;
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

    public SubmitControllerResponse from(Review review) {
      return SubmitControllerResponse.builder()
          .customerName(review.getLease().getCustomer().getName())
          .officeName(review.getLease().getOffice().getName())
          .build();
    }
  }

  @Getter
  @Builder
  public static class ReviewDto {


    String description;
    int rate;

    public static ReviewDto from(Review review) {
      return ReviewDto.builder()
          .description(review.getDescription())
          .rate(review.getRate())
          .build();
    }
  }


}