package com.dokkebi.officefinder.controller.customer;

import com.dokkebi.officefinder.controller.customer.dto.CustomerControllerDto.CustomerInfo;
import com.dokkebi.officefinder.controller.customer.dto.PointChargeHistoryDto;
import com.dokkebi.officefinder.controller.customer.dto.PointChargeRequestDto;
import com.dokkebi.officefinder.dto.PageInfo;
import com.dokkebi.officefinder.dto.PageResponseDto;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.entity.PointChargeHistory;
import com.dokkebi.officefinder.security.TokenProvider;
import com.dokkebi.officefinder.service.customer.CustomerService;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

  private final CustomerService customerService;
  private final TokenProvider tokenProvider;

  @PostMapping("/charge")
  public void chargeUserPoint(@RequestBody PointChargeRequestDto request, Principal principal) {
    customerService.chargeCustomerPoint(request.getChargeAmount(), principal.getName());
  }

  @GetMapping("/info")
  public ResponseDto<?> getCustomerInfo(@RequestHeader("Authorization") String jwtHeader) {
    Long id = tokenProvider.getUserIdFromHeader(jwtHeader);
    CustomerInfo customerInfo = customerService.getCustomerInfo(id);

    return new ResponseDto<>("success", customerInfo);
  }

  @GetMapping("/info/chargehistory")
  public PageResponseDto<?> getChargeHistoryDetails(
      @RequestHeader("Authorization") String jwtHeader,
      Pageable pageable) {
    Long id = tokenProvider.getUserIdFromHeader(jwtHeader);
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