package com.dokkebi.officefinder.service.review;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.LeaseRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.service.review.dto.ReviewServiceDto.SubmitServiceRequest;
import com.dokkebi.officefinder.service.review.dto.ReviewServiceDto.SubmitServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    Lease lease = leaseRepository.getReferenceById(submitServiceRequest.getLeaseId());
    if (!lease.getCustomer().getEmail().equals(submitServiceRequest.getCustomerEmail())) {
      throw new CustomException();
    }
    if (reviewRepository.existsByCustomerAndOffice(lease.getCustomer(), lease.getOffice())) {
      throw new CustomException();
    }
    if (!lease.getLeaseStatus().equals(LeaseStatus.EXPIRED)) {
      throw new CustomException();
    }
    Review review = Review.builder()
        .customer(lease.getCustomer())
        .office(lease.getOffice())
        .rate(submitServiceRequest.getRate())
        .description(submitServiceRequest.getDescription())
        .build();

    reviewRepository.save(review);
    return new SubmitServiceResponse().from(review);
  }


}