package com.dokkebi.officefinder.controller.customer;

import static com.dokkebi.officefinder.exception.CustomErrorCode.*;

import com.dokkebi.officefinder.controller.customer.dto.CustomerControllerDto.CustomerInfo;
import com.dokkebi.officefinder.controller.customer.dto.PointChargeHistoryDto;
import com.dokkebi.officefinder.controller.customer.dto.PointChargeRequestDto;
import com.dokkebi.officefinder.dto.PageInfo;
import com.dokkebi.officefinder.dto.PageResponseDto;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.PointChargeHistory;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.security.TokenProvider;
import com.dokkebi.officefinder.service.customer.CustomerService;
import com.dokkebi.officefinder.service.s3.S3Service;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/customers")
public class CustomerController {

  private final CustomerService customerService;
  private final TokenProvider tokenProvider;
  private final CustomerRepository customerRepository;
  private final S3Service s3Service;

  @PostMapping("/charge")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public void chargeUserPoint(@RequestBody PointChargeRequestDto request, Principal principal) {
    customerService.chargeCustomerPoint(request.getChargeAmount(), principal.getName());
  }

  @GetMapping("/info")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public ResponseDto<?> getCustomerInfo(@RequestHeader("Authorization") String jwtHeader) {
    Long id = tokenProvider.getUserIdFromHeader(jwtHeader);
    CustomerInfo customerInfo = customerService.getCustomerInfo(id);

    return new ResponseDto<>("success", customerInfo);
  }

  @PutMapping("/info/profileImage")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public void modifyProfileImage(@RequestPart("value") MultipartFile multipartFile,
      Principal principal) {

    Customer customer = customerRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND, USER_NOT_FOUND.getErrorMessage(),
            HttpStatus.BAD_REQUEST));

    if (!customer.getProfileImage().equals("None")) {
      s3Service.deleteImages(List.of(customer.getProfileImage()));
    }

    String userImagePath = s3Service.uploadImages(List.of(multipartFile)).get(0);
    customerService.changeCustomerProfileImage(userImagePath, principal.getName());
  }

  @DeleteMapping("/info/profileImage")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public void initProfileImage(Principal principal){
    Customer customer = customerRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND, USER_NOT_FOUND.getErrorMessage(),
            HttpStatus.BAD_REQUEST));

    if (!customer.getProfileImage().equals("None")) {
      s3Service.deleteImages(List.of(customer.getProfileImage()));
    }

    customerService.changeCustomerProfileImage("None", principal.getName());
  }

  @GetMapping("/info/chargehistories")
  @PreAuthorize("hasRole('ROLE_CUSTOMER')")
  public PageResponseDto<?> getChargeHistoryDetails(
      @RequestHeader("Authorization") String jwtHeader,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    Long id = tokenProvider.getUserIdFromHeader(jwtHeader);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<PointChargeHistory> histories = customerService.getAllHistories(id, pageable);

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) histories.getTotalElements(), histories.getTotalPages());

    List<PointChargeHistoryDto> list = histories.stream().map(o ->
            PointChargeHistoryDto.builder()
                .chargeAmount(o.getChargeAmount())
                .createdAt(o.getCreatedAt()).build())
        .collect(Collectors.toList());

    return new PageResponseDto<>(list, pageInfo);
  }

}