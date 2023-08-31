package com.dokkebi.officefinder.controller.review;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto;
import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.ReviewDto;
import com.dokkebi.officefinder.dto.PageInfo;
import com.dokkebi.officefinder.dto.PageResponseDto;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.security.TokenProvider;
import com.dokkebi.officefinder.service.review.ReviewService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
리뷰 CRUD API
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;
  private final TokenProvider tokenProvider;

  @PostMapping("api/customers/info/leases/{leaseId}/reviews")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ResponseDto<?> submitReview(
      @RequestBody @Valid ReviewControllerDto.SubmitControllerRequest submitControllerRequest,
      @RequestHeader("Authorization") String jwtHeader, @PathVariable @Valid Long leaseId) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwtHeader);
    Review review = reviewService.submit(submitControllerRequest, customerId, leaseId);

    return new ResponseDto<>("success", review.getId());
  }

  @GetMapping("api/customers/reviews")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public PageResponseDto<?> getCustomerReviews(@RequestHeader("Authorization") String jwtHeader,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwtHeader);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Review> reviews = reviewService.getReviewsByCustomerId(customerId, pageable);

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) reviews.getTotalElements(), reviews.getTotalPages());

    List<ReviewDto> list = reviews.stream().map(ReviewDto::from)
        .collect(Collectors.toList());

    return new PageResponseDto<>(list, pageInfo);
  }

  @PutMapping("api/customers/reviews/{reviewId}")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ResponseDto<?> updateReview(@RequestHeader("Authorization") String jwtHeader,
      @RequestBody @Valid ReviewControllerDto.SubmitControllerRequest submitControllerRequest,
      @PathVariable @Valid Long reviewId) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwtHeader);
    Review review = reviewService.update(submitControllerRequest, customerId, reviewId);

    return new ResponseDto<>("success", review.getId());
  }

  @DeleteMapping("api/customers/reviews/{reviewId}")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ResponseDto<?> deleteReview(@RequestHeader("Authorization") String jwtHeader,
      @PathVariable @Valid Long reviewId) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwtHeader);
    reviewService.delete(customerId, reviewId);

    return new ResponseDto<>("success", reviewId);
  }

}