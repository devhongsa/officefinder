package com.dokkebi.officefinder.service.review;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.SubmitControllerRequest;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
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

  public Review submit(SubmitControllerRequest controllerRequest,
      String customerEmail, Long leaseId) {
    Lease lease = leaseRepository.findById(leaseId)
        .orElseThrow(() -> new IllegalArgumentException("계약이 존재하지 않습니다."));
    if (!lease.getCustomer().getEmail().equals(customerEmail)) {
      throw new IllegalArgumentException("계약한 회원과 리뷰를 작성한 회원이 다릅니다.");
    }
    if (reviewRepository.existsByLeaseId(leaseId)) {
      throw new IllegalArgumentException("이미 리뷰가 있습니다.");
    }
    if (!lease.getLeaseStatus().equals(LeaseStatus.EXPIRED)) {
      throw new IllegalArgumentException("계약이 만료되지 않았습니다.");
    }
    Review review = Review.builder()
        .lease(lease)
        .rate(controllerRequest.getRate())
        .description(controllerRequest.getDescription())
        .build();

    return reviewRepository.save(review);
  }

  @CachePut(value = "Review", key = "#reviewId", cacheManager = "redisCacheManager")
  public Review update(SubmitControllerRequest submitControllerRequest, String customerEmail, Long reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
    if (!review.getLease().getCustomer().getEmail().equals(customerEmail)) {
      throw new IllegalArgumentException("리뷰 작성자와 수정 요청자가 다릅니다.");
    }

    Review fixedReview = Review.builder()
        .id(review.getId())
        .lease(review.getLease())
        .rate(submitControllerRequest.getRate())
        .description(submitControllerRequest.getDescription())
        .build();

    return reviewRepository.save(fixedReview);
  }

  public Page<Review> getReviewsByCustomerEmail(String customerEmail, Pageable pageable) {
    Customer customer = customerRepository.findByEmail(customerEmail)
        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    Page<Lease> leases = leaseRepository.findByCustomerId(customer.getId(), pageable);
    if (leases.isEmpty()) {
      throw new IllegalArgumentException("임대계약이 존재하지 않습니다.");
    }

    return leases.map(lease -> reviewRepository.findByLeaseId(lease.getId())
        .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다.")));
  }

  @Cacheable(value = "Review", cacheManager = "redisCacheManager")
  public List<Review> getAllReviews() {
    return reviewRepository.findAll();
  }

  @CacheEvict(value = "Review", key = "#reviewId", cacheManager = "redisCacheManager")
  public void delete(String customerEmail, Long reviewId) {
    Customer customer = customerRepository.findByEmail(customerEmail)
        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
    if (!customer.getEmail().equals(review.getLease().getCustomer().getEmail())) {
      throw new IllegalArgumentException("리뷰 작성자 본인이 아닙니다.");
    }
    reviewRepository.delete(review);
  }
}