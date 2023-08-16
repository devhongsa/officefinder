package com.dokkebi.officefinder.controller.lease;

import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.LeaseSuccessResponse;
import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.LeaseOfficeRequest;
import com.dokkebi.officefinder.service.lease.LeaseService;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseServiceResponse;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeRequestDto;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    LeaseServiceResponse serviceResponse = leaseService.leaseOffice(
        LeaseOfficeRequestDto.of(principal.getName(), officeId, req));

    return LeaseSuccessResponse.of(serviceResponse);
  }
}
