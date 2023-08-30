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
@PreAuthorize(("hasRole('OFFICE_OWNER')"))
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

  @GetMapping("/offices/{officeId}")
  public OfficeDetailResponseDto showOfficeDetail(@PathVariable("officeId") Long officeId) {
    Office office = officeQueryService.getOfficeInfo(officeId);

    return OfficeDetailResponseDto.fromEntity(office);
  }

  /**
   * 기존 오피스 사진으로 등록된 것들을 모두 삭제 -> S3 상에서 삭제 + 리포지토리에서도 삭제 이후 입력으로 받은 오피스 사진들을 다시 등록 -> S3 상에서 등록 +
   * 리포지토리에도 등록
   *
   * @param officeId
   * @param request
   * @param multipartFileList
   * @param principal
   */
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

  @GetMapping("/offices/revenue/{officeId}")
  public ResponseDto<HashMap<String,Long>> getOfficeRevenue(@PathVariable Long officeId,
      @RequestHeader("Authorization") String jwtHeader) {
    HashMap<String, Long> officeRevenue = officeOwnerService.getOfficeRevenue(officeId, jwtHeader);

    return new ResponseDto<>("success", officeRevenue);
  }

  @GetMapping("/offices/total-revenue")
  public ResponseDto<HashMap<String,Long>> getOfficesTotalRevenue(@RequestHeader("Authorization") String jwtHeader) {
    HashMap<String, Long> officeRevenue = officeOwnerService.getOfficesTotalRevenue(jwtHeader);

    return new ResponseDto<>("success", officeRevenue);
  }

  @GetMapping("/offices/rental-status/{officeId}")
  public ResponseDto<RentalStatusDto> getOfficeRentalStatus(@PathVariable Long officeId,
      @RequestHeader("Authorization") String jwtHeader) {
    RentalStatusDto officeLeaseRate = officeOwnerService.getOfficeRentalStatus(officeId, jwtHeader);

    return new ResponseDto<>("success", officeLeaseRate);
  }

  @GetMapping("/offices/overall-rental-status")
  public ResponseDto<RentalStatusDto> getOfficeOverallRentalStatus(@RequestHeader("Authorization") String jwtHeader) {
    RentalStatusDto officeLeaseRate = officeOwnerService.getOfficeOverallRentalStatus(jwtHeader);

    return new ResponseDto<>("success", officeLeaseRate);
  }
}
