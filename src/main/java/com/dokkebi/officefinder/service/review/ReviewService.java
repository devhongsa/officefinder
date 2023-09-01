package com.dokkebi.officefinder.service.review;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.SubmitControllerRequest;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.service.review.dto.ReviewOverviewDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final LeaseRepository leaseRepository;
  private final CustomerRepository customerRepository;
  private final OfficeRepository officeRepository;

  public Review submit(SubmitControllerRequest controllerRequest,
      Long customerId, Long leaseId) {
    Lease lease = leaseRepository.findById(leaseId)
        .orElseThrow(() -> new IllegalArgumentException("계약이 존재하지 않습니다."));
    if (!lease.getCustomer().getId().equals(customerId)) {
      throw new IllegalArgumentException("계약한 회원과 리뷰를 작성하려는 회원이 다릅니다.");
    }
    if (reviewRepository.existsByLeaseId(leaseId)) {
      throw new IllegalArgumentException("이미 리뷰가 있습니다.");
    }
    if (!lease.getLeaseStatus().equals(LeaseStatus.EXPIRED)) {
      throw new IllegalArgumentException("계약이 만료되지 않았습니다.");
    }
    Review review = Review.builder()
        .lease(lease)
        .customerId(customerId)
        .officeId(lease.getOffice().getId())
        .rate(controllerRequest.getRate())
        .description(controllerRequest.getDescription())
        .build();

    return reviewRepository.save(review);
  }

  @CachePut(value = "Review", key = "#reviewId", cacheManager = "redisCacheManager")
  public Review update(SubmitControllerRequest submitControllerRequest, Long customerId, Long reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
    if (!review.getLease().getCustomer().getId().equals(customerId)) {
      throw new IllegalArgumentException("리뷰 작성자와 수정 요청자가 다릅니다.");
    }

    review.updateReview(submitControllerRequest.getRate(), submitControllerRequest.getDescription());
    return reviewRepository.save(review);
  }

  @Transactional(readOnly = true)
  public Page<Review> getReviewsByCustomerId(Long customerId, Pageable pageable) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    Page<Review> reviews = reviewRepository.findByCustomerId(customerId, pageable);
    if (reviews.isEmpty()) {
      throw new IllegalArgumentException("리뷰가 존재하지 않습니다.");
    }
    return reviews;
  }

  @Transactional(readOnly = true)
  public ReviewOverviewDto getReviewOverviewByOfficeId(Long officeId) {
    List<Review> reviews = reviewRepository.findByOfficeId(officeId);
    return ReviewOverviewDto.from(reviews);
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "Review", cacheManager = "redisCacheManager")
  public List<Review> getAllReviews() {
    return reviewRepository.findAll();
  }

  @CacheEvict(value = "Review", key = "#reviewId", cacheManager = "redisCacheManager")
  public void delete(Long customerId, Long reviewId) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
    if (!customer.getId().equals(review.getLease().getCustomer().getId())) {
      throw new IllegalArgumentException("리뷰 작성자 본인이 아닙니다.");
    }
    reviewRepository.delete(review);
  }

  public Page<Review> getReviewsByOfficeId(Long officeId, Pageable pageable) {
    Office office = officeRepository.findById(officeId)
        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    Page<Review> reviews = reviewRepository.findByOfficeId(officeId, pageable);
    if (reviews.isEmpty()) {
      throw new IllegalArgumentException("리뷰가 존재하지 않습니다.");
    }
    return reviews;
  }

  public List<Review> getTopTwoReviews(Long officeId) {
    return reviewRepository.findTop2ByOfficeIdOrderByCreatedAtDesc(officeId);
  }
}