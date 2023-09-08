package com.dokkebi.officefinder.controller.customer;

import static com.dokkebi.officefinder.exception.CustomErrorCode.USER_NOT_FOUND;

import com.dokkebi.officefinder.controller.customer.dto.CustomerInfoDto;
import com.dokkebi.officefinder.controller.customer.dto.CustomerModifyDto;
import com.dokkebi.officefinder.controller.customer.dto.CustomerOverViewInfoDto;
import com.dokkebi.officefinder.controller.customer.dto.PointChargeHistoryDto;
import com.dokkebi.officefinder.controller.customer.dto.PointChargeRequestDto;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.PointChargeHistory;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.security.TokenProvider;
import com.dokkebi.officefinder.service.customer.CustomerService;
import com.dokkebi.officefinder.service.s3.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@RequestMapping("/api/customers")
public class CustomerController {

  private final CustomerService customerService;
  private final TokenProvider tokenProvider;
  private final CustomerRepository customerRepository;
  private final S3Service s3Service;

  @Operation(summary = "회원 포인트 충전", description = "회원의 포인트를 충전할 수 있다.")
  @PostMapping("/charge")
  public ResponseDto<String> chargeUserPoint(@RequestBody PointChargeRequestDto request,
      Principal principal) {

    customerService.chargeCustomerPoint(request.getChargeAmount(), principal.getName());

    return new ResponseDto<>("success", "charge success");
  }

  @Operation(summary = "회원 정보 가져오기", description = "회원 정보를 가져올 수 있다.")
  @GetMapping("/info")
  public ResponseDto<CustomerInfoDto> getCustomerInfo(
      @RequestHeader("Authorization") String jwt) {

    Long id = tokenProvider.getUserIdFromHeader(jwt);
    CustomerInfoDto customerInfoDto = customerService.getCustomerInfo(id);

    return new ResponseDto<>("success", customerInfoDto);
  }

  @Operation(summary = "회원 이름 수정", description = "회원의 이름을 수정할 수 있다.")
  @PutMapping("/info/username")
  public String changeAgentName(@RequestBody @Valid CustomerModifyDto customerModifyDto,
      @RequestHeader("Authorization") String jwt) {

    customerService.changeCustomerName(customerModifyDto.getNewName(),
        tokenProvider.getUserIdFromHeader(jwt));

    return "success";
  }

  @Operation(summary = "회원 요약 정보 조회", description = "회원 요약 정보(이름, 역할, 사진, 포인트)를 가져올 수 있다.")
  @GetMapping("/user-overviews")
  public CustomerOverViewInfoDto getCustomerOverViewInfo(
      @RequestHeader("Authorization") String jwt) {

    Long id = tokenProvider.getUserIdFromHeader(jwt);

    return customerService.getCustomerOverViewInfo(id);
  }

  @Operation(summary = "회원 이미지 등록 및 수정", description = "회원의 프로필 이미지를 등록하거나 수정할 수 있다.")
  @PutMapping("/info/profileImage")
  public ResponseDto<String> modifyProfileImage(@RequestPart("value") MultipartFile multipartFile,
      Principal principal) {

    Customer customer = customerRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (!customer.getProfileImage().equals("None")) {
      s3Service.deleteImages(List.of(customer.getProfileImage()));
    }

    String userImagePath = s3Service.uploadImages(List.of(multipartFile)).get(0);
    customerService.changeCustomerProfileImage(userImagePath, principal.getName());

    return new ResponseDto<>("success", "image modify success");
  }

  @Operation(summary = "회원 프로필 이미지 초기화", description = "회원의 프로필 이미지를 기본 이미지로 초기화한다.")
  @DeleteMapping("/info/profileImage")
  public ResponseDto<String> initProfileImage(Principal principal) {
    Customer customer = customerRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    if (!customer.getProfileImage().equals("None")) {
      s3Service.deleteImages(List.of(customer.getProfileImage()));
    }

    customerService.changeCustomerProfileImage("None", principal.getName());

    return new ResponseDto<>("success", "image modify success");
  }

  @Operation(summary = "회원의 포인트 충전 이력 조회", description = "회원의 포인트 충전 내역을 가져올 수 있다.")
  @GetMapping("/info/charge-histories")
  public Page<PointChargeHistoryDto> getChargeHistoryDetails(
      @RequestHeader("Authorization") String jwt,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size
  ) {
    Long id = tokenProvider.getUserIdFromHeader(jwt);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<PointChargeHistory> histories = customerService.getAllHistories(id, pageable);

    return histories.map(PointChargeHistoryDto::from);
  }

}