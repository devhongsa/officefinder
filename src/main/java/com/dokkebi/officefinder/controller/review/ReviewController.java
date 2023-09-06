package com.dokkebi.officefinder.controller.review;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto;
import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.ReviewDto;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.security.TokenProvider;
import com.dokkebi.officefinder.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  private final CustomerRepository customerRepository;

  @Operation(summary = "리뷰 등록", description = "회원은 만기된 임대에 대한 리뷰를 등록할 수 있다.")
  @PostMapping("api/customers/info/leases/{leaseId}/reviews")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseDto<Long> submitReview(
      @RequestBody @Valid ReviewControllerDto.SubmitControllerRequest submitControllerRequest,
      @CookieValue("Authorization") String jwt, @PathVariable @Valid Long leaseId) {
    Long customerId = tokenProvider.getUserId(jwt);
    Review review = reviewService.submit(submitControllerRequest, customerId, leaseId);

    return new ResponseDto<>("success", review.getId());
  }

  @Operation(summary = "리뷰 조회", description = "자신이 등록한 리뷰를 조회할 수 있다.")
  @GetMapping("api/customers/reviews")
  @PreAuthorize("hasRole('CUSTOMER')")
  public Page<ReviewDto> getCustomerReviews(@CookieValue("Authorization") String jwt,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    Long customerId = tokenProvider.getUserId(jwt);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<Review> reviews = reviewService.getReviewsByCustomerId(customerId, pageable);

    return reviews.map(content -> ReviewDto.from(content,
        customerRepository.findById(customerId).orElseThrow(() -> new CustomException(
            CustomErrorCode.USER_NOT_FOUND))));
  }

  @Operation(summary = "리뷰 수정", description = "자신이 등록한 리뷰를 수정할 수 있다.")
  @PutMapping("api/customers/reviews/{reviewId}")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseDto<Long> updateReview(@CookieValue("Authorization") String jwt,
      @RequestBody @Valid ReviewControllerDto.SubmitControllerRequest submitControllerRequest,
      @PathVariable @Valid Long reviewId) {
    Long customerId = tokenProvider.getUserId(jwt);
    Review review = reviewService.update(submitControllerRequest, customerId, reviewId);

    return new ResponseDto<>("success", review.getId());
  }

  @Operation(summary = "리뷰 삭제", description = "자신이 등록한 리뷰를 삭제할 수 있다.")
  @DeleteMapping("api/customers/reviews/{reviewId}")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseDto<Long> deleteReview(@CookieValue("Authorization") String jwt,
      @PathVariable @Valid Long reviewId) {
    Long customerId = tokenProvider.getUserId(jwt);
    reviewService.delete(customerId, reviewId);

    return new ResponseDto<>("success", reviewId);
  }

}