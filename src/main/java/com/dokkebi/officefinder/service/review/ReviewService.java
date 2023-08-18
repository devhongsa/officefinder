package com.dokkebi.officefinder.service.review;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.repository.LeaseRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.service.review.dto.ReviewServiceDto.UpdateServiceRequest;
import com.dokkebi.officefinder.service.review.dto.ReviewServiceDto.SubmitServiceRequest;
import com.dokkebi.officefinder.service.review.dto.ReviewServiceDto.SubmitServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final LeaseRepository leaseRepository;

  public SubmitServiceResponse submit(SubmitServiceRequest submitServiceRequest) {
    Lease lease = leaseRepository.findById(submitServiceRequest.getLeaseId())
        .orElseThrow(() -> new IllegalArgumentException("계약이 존재하지 않습니다."));
    if (!lease.getCustomer().getEmail().equals(submitServiceRequest.getCustomerEmail())) {
      throw new IllegalArgumentException("계약한 회원과 리뷰를 작성한 회원이 다릅니다.");
    }
    if (reviewRepository.existsByLeaseId(lease.getId())) {
      throw new IllegalArgumentException("이미 리뷰가 있습니다.");
    }
    if (!lease.getLeaseStatus().equals(LeaseStatus.EXPIRED)) {
      throw new IllegalArgumentException("계약이 만료되지 않았습니다.");
    }
    Review review = Review.builder()
        .lease(lease)
        .rate(submitServiceRequest.getRate())
        .description(submitServiceRequest.getDescription())
        .build();

    reviewRepository.save(review);
    return new SubmitServiceResponse().from(review);
  }

  @CachePut(value = "Review", key = "#reviewId", cacheManager = "redisCacheManager")
  public void update(UpdateServiceRequest updateServiceRequest, Long reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
    if (!review.getLease().getCustomer().getEmail().equals(updateServiceRequest.getCustomerEmail())) {
      throw new IllegalArgumentException("리뷰 작성자와 수정 요청자가 다릅니다.");
    }

    Review fixedReview = Review.builder()
        .id(review.getId())
        .lease(review.getLease())
        .rate(updateServiceRequest.getRate())
        .description(updateServiceRequest.getDescription())
        .build();

    reviewRepository.save(fixedReview);
  }

}