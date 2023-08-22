package com.dokkebi.officefinder.controller.officeowner;

import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeDetailResponseDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeModifyRequestDto;
import com.dokkebi.officefinder.controller.officeowner.dto.OwnerOfficeOverViewDto;
import com.dokkebi.officefinder.dto.PageInfo;
import com.dokkebi.officefinder.dto.PageResponseDto;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.service.office.OfficeSearchService;
import com.dokkebi.officefinder.service.office.OfficeService;
import com.dokkebi.officefinder.service.s3.S3Service;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/agents")
public class OfficeOwnerController {

  private final OfficeService officeService;
  private final OfficeSearchService officeQueryService;
  private final S3Service s3Service;

  @ApiOperation(value = "오피스 리스트 조회", notes = "자신이 등록한 오피스 리스트를 조회할 수 있다.")
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

  @ApiOperation(value = "오피스 등록", notes = "자신이 가진 오피스를 서비스에 등록할 수 있다.")
  @PostMapping("/offices")
  public void addOffice(
      @RequestPart(value = "request") OfficeCreateRequestDto request,
      @RequestPart(value = "multipartFileList") List<MultipartFile> multipartFileList,
      Principal principal
  ) {
    List<String> imagePaths = s3Service.uploadOfficeImages(multipartFileList);
    officeService.createOfficeInfo(request, imagePaths, principal.getName());
  }

  @GetMapping("/offices/{officeId}")
  public OfficeDetailResponseDto showOfficeDetail(@PathVariable("officeId") Long officeId) {
    Office office = officeQueryService.getOfficeInfo(officeId);

    return OfficeDetailResponseDto.fromEntity(office);
  }

  @PutMapping("/offices/{officeId}")
  public void modifyOffice(
      @PathVariable("officeId") Long officeId,
      @RequestPart(value = "request") OfficeModifyRequestDto request,
      @RequestPart(value = "multipartFileList") List<MultipartFile> multipartFileList,
      Principal principal
  ) {
    officeService.modifyOfficeInfo(request, principal.getName(), officeId);
  }
}
