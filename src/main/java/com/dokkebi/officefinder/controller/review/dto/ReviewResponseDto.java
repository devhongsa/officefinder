package com.dokkebi.officefinder.controller.review.dto;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.ReviewDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewResponseDto {

  private String reviewAmount;
  private List<ReviewDto> data;
}
