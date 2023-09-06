package com.dokkebi.officefinder.service.review;

import static com.dokkebi.officefinder.exception.CustomErrorCode.LEASE_NOT_EXPIRED;
import static com.dokkebi.officefinder.exception.CustomErrorCode.LEASE_NOT_FOUND;
import static com.dokkebi.officefinder.exception.CustomErrorCode.LEASE_OWNER_NOT_MATCH;
import static com.dokkebi.officefinder.exception.CustomErrorCode.OFFICE_NOT_EXISTS;
import static com.dokkebi.officefinder.exception.CustomErrorCode.REVIEW_ALREADY_EXISTS;
import static com.dokkebi.officefinder.exception.CustomErrorCode.REVIEW_NOT_EXISTS;
import static com.dokkebi.officefinder.exception.CustomErrorCode.REVIEW_OWNER_NOT_MATCH;
import static com.dokkebi.officefinder.exception.CustomErrorCode.USER_NOT_FOUND;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.SubmitControllerRequest;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.exception.CustomException;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final LeaseRepository leaseRepository;
  private final CustomerRepository customerRepository;
  private final OfficeRepository officeRepository;

  @Transactional
  public Review submit(SubmitControllerRequest controllerRequest,
      Long customerId, Long leaseId) {
    Lease lease = leaseRepository.findById(leaseId)
        .orElseThrow(() -> new CustomException(LEASE_NOT_FOUND));

    if (!lease.getCustomer().getId().equals(customerId)) {
      throw new CustomException(LEASE_OWNER_NOT_MATCH);
    }

    if (reviewRepository.existsByLeaseId(leaseId)) {
      throw new CustomException(REVIEW_ALREADY_EXISTS);
    }

    if (!lease.getLeaseStatus().equals(LeaseStatus.EXPIRED)) {
      throw new CustomException(LEASE_NOT_EXPIRED);
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

  @Transactional
  @CachePut(value = "Review", key = "#reviewId", cacheManager = "redisCacheManager")
  public Review update(SubmitControllerRequest submitControllerRequest, Long customerId, Long reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(REVIEW_NOT_EXISTS));

    if (!review.getLease().getCustomer().getId().equals(customerId)) {
      throw new CustomException(REVIEW_OWNER_NOT_MATCH);
    }

    review.updateReview(submitControllerRequest.getRate(), submitControllerRequest.getDescription());
    return reviewRepository.save(review);
  }

  public Page<Review> getReviewsByCustomerId(Long customerId, Pageable pageable) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Page<Review> reviews = reviewRepository.findByCustomerId(customerId, pageable);

    if (reviews.isEmpty()) {
      throw new CustomException(REVIEW_NOT_EXISTS);
    }

    return reviews;
  }

  public ReviewOverviewDto getReviewOverviewByOfficeId(Long officeId) {
    List<Review> reviews = reviewRepository.findByOfficeId(officeId);
    return ReviewOverviewDto.from(reviews);
  }

  @Cacheable(value = "Review", cacheManager = "redisCacheManager")
  public List<Review> getAllReviews() {
    return reviewRepository.findAll();
  }

  @CacheEvict(value = "Review", key = "#reviewId", cacheManager = "redisCacheManager")
  public void delete(Long customerId, Long reviewId) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(REVIEW_NOT_EXISTS));

    if (!customer.getId().equals(review.getLease().getCustomer().getId())) {
      throw new CustomException(REVIEW_OWNER_NOT_MATCH);
    }

    reviewRepository.delete(review);
  }

  public Page<Review> getReviewsByOfficeId(Long officeId, Pageable pageable) {
    Office office = officeRepository.findById(officeId)
        .orElseThrow(() -> new CustomException(OFFICE_NOT_EXISTS));

    Page<Review> reviews = reviewRepository.findByOfficeId(officeId, pageable);

    if (reviews.isEmpty()) {
      throw new CustomException(REVIEW_NOT_EXISTS);
    }

    return reviews;
  }

  public List<Review> getTopTwoReviews(Long officeId) {
    return reviewRepository.findTop2ByOfficeIdOrderByCreatedAtDesc(officeId);
  }
}