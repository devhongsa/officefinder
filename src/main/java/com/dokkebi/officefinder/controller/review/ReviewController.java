package com.dokkebi.officefinder.controller.review;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto;
import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.ReviewDto;
import com.dokkebi.officefinder.dto.PageInfo;
import com.dokkebi.officefinder.dto.PageResponseDto;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
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

  @Operation(summary = "리뷰 등록", description = "회원은 만기된 임대에 대한 리뷰를 등록할 수 있다.")
  @PostMapping("api/customers/info/leases/{leaseId}/reviews")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseDto<?> submitReview(
      @RequestBody @Valid ReviewControllerDto.SubmitControllerRequest submitControllerRequest,
      HttpServletRequest request, @PathVariable @Valid Long leaseId) {
    String customerEmail = request.getUserPrincipal().getName();
    Review review = reviewService.submit(submitControllerRequest, customerEmail, leaseId);

    return new ResponseDto<>("success", review.getId());
  }

  @Operation(summary = "리뷰 조회", description = "자신이 등록한 리뷰를 조회할 수 있다.")
  @GetMapping("api/customers/reviews")
  @PreAuthorize("hasRole('CUSTOMER')")
  public PageResponseDto<?> getReviews(HttpServletRequest request,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size
  ) {
    String customerEmail = request.getUserPrincipal().getName();
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Review> reviews = reviewService.getReviewsByCustomerEmail(customerEmail, pageable);

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) reviews.getTotalElements(), reviews.getTotalPages());

    List<ReviewDto> list = reviews.stream().map(ReviewDto::from)
        .collect(Collectors.toList());

    return new PageResponseDto<>(list, pageInfo);
  }

  @Operation(summary = "리뷰 수정", description = "자신이 등록한 리뷰를 수정할 수 있다.")
  @PutMapping("api/customers/reviews/{reviewId}")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseDto<?> updateReview(
      @RequestBody @Valid ReviewControllerDto.SubmitControllerRequest submitControllerRequest,
      HttpServletRequest request, @PathVariable @Valid Long reviewId
  ) {
    String customerEmail = request.getUserPrincipal().getName();
    Review review = reviewService.update(submitControllerRequest, customerEmail, reviewId);

    return new ResponseDto<>("success", review.getId());
  }

  @Operation(summary = "리뷰 삭제", description = "자신이 등록한 리뷰를 삭제할 수 있다.")
  @DeleteMapping("api/customers/reviews/{reviewId}")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseDto<?> deleteReview(HttpServletRequest request,
      @PathVariable @Valid Long reviewId) {
    String customerEmail = request.getUserPrincipal().getName();
    reviewService.delete(customerEmail, reviewId);

    return new ResponseDto<>("success", reviewId);
  }

}