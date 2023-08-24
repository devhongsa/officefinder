package com.dokkebi.officefinder.controller.lease;

import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.AgentLeaseLookUpResponse;
import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.LeaseSuccessResponse;
import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.LeaseOfficeRequest;
import com.dokkebi.officefinder.dto.PageInfo;
import com.dokkebi.officefinder.dto.PageResponseDto;
import com.dokkebi.officefinder.service.lease.LeaseService;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseLookUpServiceResponse;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeServiceResponse;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeRequestDto;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LeaseController {

  private final LeaseService leaseService;

  @PostMapping("/offices/{officeId}")
  public LeaseSuccessResponse leaseOffice(Principal principal, @PathVariable Long officeId,
      @RequestBody LeaseOfficeRequest req) {

    LeaseOfficeServiceResponse serviceResponse = leaseService.leaseOffice(
        LeaseOfficeRequestDto.of(principal.getName(), officeId, req));

    return LeaseSuccessResponse.of(serviceResponse);
  }

  @GetMapping("/customers/info/leases")
  public PageResponseDto<?> getLeaseInfo(Principal principal, Pageable pageableReceived) {

    Pageable pageable = createPageable(pageableReceived, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<LeaseLookUpServiceResponse> serviceResponses = leaseService.getLeaseList(
        principal.getName(), pageable);

    return createPageResponseDto(serviceResponses);
  }

  @GetMapping("/agents/offices/{officeId}/lease-requests")
  public PageResponseDto<?> getLeaseRequest(Principal principal,
      @PathVariable Long officeId, Pageable pageableReceived) {

    Pageable pageable = createPageable(pageableReceived, Sort.by("createdAt"));

    Page<AgentLeaseLookUpResponse> leaseRequestList = leaseService.getLeaseRequestList(
        principal.getName(), officeId, pageable);

    return createPageResponseDto(leaseRequestList);
  }

  @PutMapping("/agents/office/lease-requests/{leaseId}/accept")
  public ResponseEntity acceptRequest(Principal principal, @PathVariable Long leaseId){

    leaseService.acceptLeaseRequest(leaseId);

    return ResponseEntity.ok().build();
  }

  private Pageable createPageable(Pageable pageableReceived, Sort sort) {
    return PageRequest.of(pageableReceived.getPageNumber(), pageableReceived.getPageSize(), sort);
  }

  private <T> PageResponseDto<List<T>> createPageResponseDto(Page<T> pageData) {

    PageInfo pageInfo = new PageInfo(pageData.getNumber(), pageData.getSize(),
        (int) pageData.getTotalElements(), pageData.getTotalPages());

    return new PageResponseDto<>(pageData.getContent(), pageInfo);
  }
}
