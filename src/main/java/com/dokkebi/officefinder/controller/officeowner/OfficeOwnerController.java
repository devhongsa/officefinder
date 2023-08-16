package com.dokkebi.officefinder.controller.officeowner;

import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeDetailResponseDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeModifyRequestDto;
import com.dokkebi.officefinder.controller.officeowner.dto.OwnerOfficeOverViewDto;
import com.dokkebi.officefinder.dto.PageInfo;
import com.dokkebi.officefinder.dto.PageResponseDto;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.service.office.OfficeQueryService;
import com.dokkebi.officefinder.service.office.OfficeRedisService;
import com.dokkebi.officefinder.service.office.OfficeService;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/agents")
public class OfficeOwnerController {

  private final OfficeService officeService;
  private final OfficeQueryService officeQueryService;
  private final OfficeRedisService officeRedisService;

  @GetMapping("/offices")
  public PageResponseDto<?> showOfficeList(Principal principal, Pageable pageable) {
    Page<Office> result = officeQueryService.getAllOffices(principal.getName(), pageable);

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) result.getTotalElements(), result.getTotalPages());

    List<OwnerOfficeOverViewDto> overViewData = result.getContent().stream()
        .map(OwnerOfficeOverViewDto::fromEntity)
        .collect(Collectors.toList());

    return new PageResponseDto<>(overViewData, pageInfo);
  }

  @PostMapping("/offices")
  public void addOffice(@RequestBody OfficeCreateRequestDto request, Principal principal) {
    officeService.createOfficeInfo(request, principal.getName());
  }

  @GetMapping("/offices/{officeId}")
  public OfficeDetailResponseDto showOfficeDetail(@PathVariable("officeId") Long officeId) {
    Office office = officeQueryService.getOfficeInfo(officeId);

    return OfficeDetailResponseDto.fromEntity(office,
        officeRedisService.getRemainRoom(office.getName()));
  }

  @PutMapping("/offices/{officeId}")
  public void modifyOffice(
      @PathVariable("officeId") Long officeId,
      @RequestBody OfficeModifyRequestDto request,
      Principal principal
  ) {
    officeService.modifyOfficeInfo(request, principal.getName(), officeId);
  }
}
