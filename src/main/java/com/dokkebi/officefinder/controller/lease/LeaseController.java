package com.dokkebi.officefinder.controller.lease;

import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.AgentLeaseLookUpResponse;
import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.LeaseLookUpResponse;
import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.LeaseSuccessResponse;
import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.LeaseOfficeRequest;
import com.dokkebi.officefinder.service.lease.LeaseService;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseLookUpServiceResponse;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeServiceResponse;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeRequestDto;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public Page<LeaseLookUpResponse> getLeaseInfo(Principal principal,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {

    // 기본적으로 임대 정보가 생성 되었던 시간에 따라 내림차순으로 정렬
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<LeaseLookUpServiceResponse> serviceResponses = leaseService.getLeaseList(
        principal.getName(), pageable);

    return serviceResponses.map(LeaseLookUpResponse::of);
  }

  @GetMapping("/agents/offices/{officeId}/lease-requests")
  public Page<AgentLeaseLookUpResponse> getLeaseRequest(Principal principal,
      @PathVariable Long officeId, @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "5") Integer size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));

    return leaseService.getLeaseRequestList(principal.getName(), officeId, pageable);
  }
}
