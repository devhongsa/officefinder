package com.dokkebi.officefinder.controller.office;

import com.dokkebi.officefinder.controller.office.dto.OfficeDetailResponseDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeDetailSearchCond;
import com.dokkebi.officefinder.controller.office.dto.OfficeOverViewDto;
import com.dokkebi.officefinder.dto.PageInfo;
import com.dokkebi.officefinder.dto.PageResponseDto;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.service.office.OfficeQueryService;
import com.dokkebi.officefinder.service.office.OfficeRedisService;
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

  private final OfficeQueryService officeQueryService;
  private final OfficeRedisService officeRedisService;

  @GetMapping
  public PageResponseDto<?> showOfficeList(OfficeDetailSearchCond cond, Pageable pageable) {
    Page<Office> offices = officeQueryService.searchOfficeByDetailCondition(cond, pageable);

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) offices.getTotalElements(), offices.getTotalPages());

    List<OfficeOverViewDto> officeOverViewList = offices.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, 0, 0.0))
        .collect(Collectors.toList());

    return new PageResponseDto<>(officeOverViewList, pageInfo);
  }

  @GetMapping("/{officeId}")
  public OfficeDetailResponseDto showOfficeDetail(@PathVariable("officeId") Long officeId) {
    Office officeInfo = officeQueryService.getOfficeInfo(officeId);

    return OfficeDetailResponseDto.fromEntity(officeInfo,
        officeRedisService.getRemainRoom(officeInfo.getName()));
  }
}
