package com.dokkebi.officefinder.controller.office;

import com.dokkebi.officefinder.controller.office.dto.OfficeDetailResponseDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeOverViewDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeSearchCond;
import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.ReviewDto;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficePicture;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.office.picture.OfficePictureRepository;
import com.dokkebi.officefinder.service.office.OfficeSearchService;
import com.dokkebi.officefinder.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/offices")
public class OfficeController {

  private final OfficeSearchService officeQueryService;
  private final OfficePictureRepository officePictureRepository;
  private final CustomerRepository customerRepository;
  private final ReviewService reviewService;

  @Operation(summary = "오피스 검색", description = "오피스를 특정 조건에 맞게 검색할 수 있다.")
  @GetMapping
  public Page<OfficeOverViewDto> showOfficeList(OfficeSearchCond cond,
      Pageable pageable) {

    Page<Office> offices = officeQueryService.searchOfficeByDetailCondition(cond, pageable);

    return offices.map(
        content -> OfficeOverViewDto.fromEntity(content, reviewService.getReviewOverviewByOfficeId(
                content.getId()),
            officePictureRepository.findByOfficeId(content.getId()).get(0).getFileName()));
  }

  @Operation(summary = "오피스 조회", description = "특정 오피스를 조회할 수 있다.")
  @GetMapping("/{officeId}")
  public OfficeDetailResponseDto showOfficeDetail(@PathVariable("officeId") Long officeId) {
    Office officeInfo = officeQueryService.getOfficeInfo(officeId);
    List<OfficePicture> officeImages = officePictureRepository.findByOfficeId(officeId);

    List<String> imagePath = new ArrayList<>();

    for (OfficePicture element: officeImages){
      imagePath.add(element.getFileName());
    }

    while(imagePath.size() < 5){
      imagePath.add("None");
    }

    List<Review> reviews = reviewService.getTopTwoReviews(officeId);

    List<ReviewDto> reviewDtoList = reviews.stream()
        .map(content -> ReviewDto.from(content, customerRepository.findById(content.getCustomerId())
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND))))
        .collect(Collectors.toList());

    return OfficeDetailResponseDto.from(officeInfo, reviewDtoList, imagePath);
  }

  @Operation(summary = "오피스 리뷰조회", description = "특정 오피스의 리뷰를 조회할 수 있다.")
  @GetMapping("/{officeId}/reviews")
  public Page<ReviewDto> getOfficeReviews(@PathVariable @Valid Long officeId,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

    Page<Review> reviews = reviewService.getReviewsByOfficeId(officeId, pageable);

    return reviews.map(content -> ReviewDto.from(content,
        customerRepository.findById(content.getCustomerId()).orElseThrow(() -> new CustomException(
            CustomErrorCode.USER_NOT_FOUND))));
  }
}