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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
리뷰 CRUD API
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping("/submit")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseDto<?> submitReview(@RequestBody @Valid ReviewControllerDto.SubmitControllerRequest submitControllerRequest,
      Principal principal) {
    String customerEmail = principal.getName();
    SubmitServiceRequest serviceRequest = new SubmitServiceRequest().from(submitControllerRequest, customerEmail);
    SubmitServiceResponse submitServiceResponse = reviewService.submit(serviceRequest);

    SubmitControllerResponse submitControllerResponse = new SubmitControllerResponse().from(submitServiceResponse);
    log.info("review submit from : " + submitControllerResponse.getCustomerName()
        + ", to : " + submitControllerResponse.getOfficeName());

    return new ResponseDto<>("success", submitControllerResponse);
  }

  @PostMapping("/fix")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseDto<?> fixReview(@RequestBody @Valid ReviewControllerDto.SubmitControllerRequest submitControllerRequest,
      Principal principal) {
    String customerEmail = principal.getName();
    SubmitServiceRequest serviceRequest = new SubmitServiceRequest().from(submitControllerRequest, customerEmail);
    SubmitServiceResponse submitServiceResponse = reviewService.submit(serviceRequest);

    SubmitControllerResponse submitControllerResponse = new SubmitControllerResponse().from(submitServiceResponse);
    log.info("review fix from : " + submitControllerResponse.getCustomerName()
        + ", to : " + submitControllerResponse.getOfficeName());

    return new ResponseDto<>("success", submitControllerResponse);
  }

}