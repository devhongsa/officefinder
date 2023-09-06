package com.dokkebi.officefinder.controller.review.dto;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.review.Review;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
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
  @AllArgsConstructor
  @Builder
  public static class ReviewDto {

    private String customerName;
    private String customerImagePath;
    private LocalDate createdAt;
    private String description;
    private int rate;

    public static ReviewDto from(Review review, Customer customer) {
      return ReviewDto.builder()
          .customerName(customer.getName())
          .customerImagePath(customer.getProfileImage())
          .createdAt(review.getCreatedAt().toLocalDate())
          .description(review.getDescription())
          .rate(review.getRate())
          .build();
    }
  }


}