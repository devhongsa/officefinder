package com.dokkebi.officefinder.controller.officeowner;

import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeDetailResponseDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeModifyRequestDto;
import com.dokkebi.officefinder.controller.officeowner.dto.OwnerOfficeOverViewDto;
import com.dokkebi.officefinder.dto.PageInfo;
import com.dokkebi.officefinder.dto.PageResponseDto;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficePicture;
import com.dokkebi.officefinder.repository.office.picture.OfficePictureRepository;
import com.dokkebi.officefinder.service.office.OfficeSearchService;
import com.dokkebi.officefinder.service.office.OfficeService;
import com.dokkebi.officefinder.service.officeowner.dto.OfficeOwnerServiceDto.RentalStatusDto;
import com.dokkebi.officefinder.service.s3.S3Service;
import com.dokkebi.officefinder.service.officeowner.OfficeOwnerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/agents")
@Slf4j
@PreAuthorize("hasRole('OFFICE_OWNER')")
public class OfficeOwnerController {

  private final OfficeService officeService;
  private final OfficeSearchService officeQueryService;
  private final OfficePictureRepository officePictureRepository;
  private final S3Service s3Service;
  private final OfficeOwnerService officeOwnerService;

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
    List<String> imagePaths = s3Service.uploadImages(multipartFileList);
    officeService.createOfficeInfo(request, imagePaths, principal.getName());
  }

  @Operation(summary = "오피스 상세 조회", description = "자신이 등록한 오피스의 상세 정보롤 볼 수 있다.")
  @GetMapping("/offices/{officeId}")
  public OfficeDetailResponseDto showOfficeDetail(@PathVariable("officeId") Long officeId) {
    Office office = officeQueryService.getOfficeInfo(officeId);

    return OfficeDetailResponseDto.fromEntity(office);
  }

  @Operation(summary = "오피스 정보 수정", description = "자신의 오피스 정보를 수정할 수 있다.")
  @PutMapping("/offices/{officeId}")
  public void modifyOffice(
      @PathVariable("officeId") Long officeId,
      @RequestPart(value = "request") OfficeModifyRequestDto request,
      @RequestPart(value = "multipartFileList") List<MultipartFile> multipartFileList,
      Principal principal
  ) {

    // 기존 이미지 삭제
    List<OfficePicture> officePicture = officePictureRepository.findByOfficeId(officeId);
    if (officePicture != null && !officePicture.isEmpty()) {
      List<String> fileList = officePicture.stream()
          .map(OfficePicture::getFileName)
          .collect(Collectors.toList());

      s3Service.deleteImages(fileList);
    }

    // 들어온 이미지 등록
    List<String> imagePaths = s3Service.uploadImages(multipartFileList);
    officeService.modifyOfficeInfo(request, imagePaths, principal.getName(), officeId);
  }

  @Operation(summary = "해당 오피스의 매출 조회", description = "특정 오피스의 매출을 가져올 수 있다.")
  @GetMapping("/offices/revenue/{officeId}")
  public ResponseDto<HashMap<String,Long>> getOfficeRevenue(@PathVariable Long officeId,
      @RequestHeader("Authorization") String jwtHeader) {
    HashMap<String, Long> officeRevenue = officeOwnerService.getOfficeRevenue(officeId, jwtHeader);

    return new ResponseDto<>("success", officeRevenue);
  }

  @Operation(summary = "오피스 전체 매출 조회", description = "임대주가 가진 모든 오피스의 매출 합을 가져올 수 있다.")
  @GetMapping("/offices/total-revenue")
  public ResponseDto<HashMap<String,Long>> getOfficesTotalRevenue(@RequestHeader("Authorization") String jwtHeader) {
    HashMap<String, Long> officeRevenue = officeOwnerService.getOfficesTotalRevenue(jwtHeader);

    return new ResponseDto<>("success", officeRevenue);
  }


  @Operation(summary = "오피스 임대 현황 조회", description = "특정 오피스의 임대 현황을 조회할 수 있다.")
  @GetMapping("/offices/rental-status/{officeId}")
  public ResponseDto<RentalStatusDto> getOfficeRentalStatus(@PathVariable Long officeId,
      @RequestHeader("Authorization") String jwtHeader) {
    RentalStatusDto officeLeaseRate = officeOwnerService.getOfficeRentalStatus(officeId, jwtHeader);

    return new ResponseDto<>("success", officeLeaseRate);
  }

  @Operation(summary = "오피스 총 임대 현황 조회", description = "모든 오피스의 임대 현황을 조회할 수 있다.")
  @GetMapping("/offices/overall-rental-status")
  public ResponseDto<RentalStatusDto> getOfficeOverallRentalStatus(@RequestHeader("Authorization") String jwtHeader) {
    RentalStatusDto officeLeaseRate = officeOwnerService.getOfficeOverallRentalStatus(jwtHeader);

    return new ResponseDto<>("success", officeLeaseRate);
  }
}
