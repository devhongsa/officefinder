package com.dokkebi.officefinder.controller.office;

import com.dokkebi.officefinder.controller.office.dto.OfficeDetailResponseDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeSearchCond;
import com.dokkebi.officefinder.controller.office.dto.OfficeOverViewDto;
import com.dokkebi.officefinder.dto.PageInfo;
import com.dokkebi.officefinder.dto.PageResponseDto;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.service.office.OfficeSearchService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/offices")
public class OfficeController {

  private final OfficeSearchService officeQueryService;

  @Operation(summary = "오피스 검색", description = "오피스를 특정 조건에 맞게 검색할 수 있다.")
  @GetMapping
  public PageResponseDto<?> showOfficeList(OfficeSearchCond cond, Pageable pageable) {
    Page<Office> offices = officeQueryService.searchOfficeByDetailCondition(cond, pageable);

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) offices.getTotalElements(), offices.getTotalPages());

    List<OfficeOverViewDto> officeOverViewList = offices.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, 0, 0.0))
        .collect(Collectors.toList());

    return new PageResponseDto<>(officeOverViewList, pageInfo);
  }

  @Operation(summary = "오피스 조회", description = "특정 오피스를 조회할 수 있다.")
  @GetMapping("/{officeId}")
  public ResponseDto<?> showOfficeDetail(@PathVariable("officeId") Long officeId) {
    Office officeInfo = officeQueryService.getOfficeInfo(officeId);

    return new ResponseDto<>("success", OfficeDetailResponseDto.fromEntity(officeInfo));
  }
}
