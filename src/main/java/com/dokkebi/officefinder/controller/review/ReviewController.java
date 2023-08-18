package com.dokkebi.officefinder.controller.review;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto;
import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.SubmitControllerResponse;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.service.review.ReviewService;
import com.dokkebi.officefinder.service.review.dto.ReviewServiceDto.SubmitServiceRequest;
import com.dokkebi.officefinder.service.review.dto.ReviewServiceDto.SubmitServiceResponse;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
리뷰 CRUD API
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping("api/customers/info/leases/{leaseId}/reviews")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseDto<?> submitReview(@RequestBody @Valid ReviewControllerDto.SubmitControllerRequest submitControllerRequest,
      Principal principal, @PathVariable @Valid Long leaseId) {
    String customerEmail = principal.getName();
    SubmitServiceRequest serviceRequest = new SubmitServiceRequest().from(submitControllerRequest, customerEmail, leaseId);
    SubmitServiceResponse submitServiceResponse = reviewService.submit(serviceRequest);

    SubmitControllerResponse submitControllerResponse = new SubmitControllerResponse().from(submitServiceResponse);
    log.info("review submit from : " + submitControllerResponse.getCustomerName()
        + ", to : " + submitControllerResponse.getOfficeName());

    return new ResponseDto<>("success", submitControllerResponse);
  }


}