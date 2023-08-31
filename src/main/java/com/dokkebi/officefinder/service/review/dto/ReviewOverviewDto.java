package com.dokkebi.officefinder.service.review.dto;

import com.dokkebi.officefinder.entity.review.Review;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class ReviewOverviewDto {
  private int reviewCount;
  private double reviewRate;

  public ReviewOverviewDto(int reviewCount, double reviewRate) {
    this.reviewCount = reviewCount;
    this.reviewRate = reviewRate;
  }

  public static ReviewOverviewDto from(List<Review> reviews) {
      Double reviewRate = reviews.stream().mapToDouble(Review::getRate)
          .average().orElse(0);
    return ReviewOverviewDto.builder()
        .reviewCount(reviews.size())
        .reviewRate(reviewRate)
        .build();
  }
}