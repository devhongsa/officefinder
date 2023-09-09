package com.dokkebi.officefinder.service.lease.dto;

import com.dokkebi.officefinder.controller.lease.dto.LeaseControllerDto.LeaseOfficeRequest;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficePicture;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LeaseServiceDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class LeaseOfficeRequestDto {

    private String email;

    private Long officeId;

    private LocalDate startDate;

    private int months;

    private int customerCount;

    public static LeaseOfficeRequestDto of(String email, Long officeId,
        LeaseOfficeRequest request) {
      return LeaseOfficeRequestDto.builder()
          .email(email)
          .officeId(officeId)
          .startDate(LocalDate.parse(request.getStartDate()))
          .months(request.getMonths())
          .customerCount(request.getCustomerCount())
          .build();
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class LeaseOfficeServiceResponse {

    private Long leaseId;

    private String customerEmail;

    private String officeName;

    private long price;

    private LeaseStatus leaseStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    public static LeaseOfficeServiceResponse of(Lease lease) {
      return LeaseOfficeServiceResponse.builder()
          .leaseId(lease.getId())
          .customerEmail(lease.getCustomer().getEmail())
          .officeName(lease.getOffice().getName())
          .price(lease.getPrice())
          .leaseStatus(lease.getLeaseStatus())
          .startDate(lease.getLeaseStartDate())
          .endDate(lease.getLeaseEndDate())
          .build();
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class LeaseLookUpServiceResponse {

    private Long leaseId;

    private String name;

    private String location;

    private LeaseStatus leaseStatus;

    private LocalDate paymentDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<String> officeImagePath;

    private boolean isReviewed;

    public static LeaseLookUpServiceResponse of(Lease lease, boolean isReviewed,
        List<OfficePicture> imagePaths) {

      List<String> imageList = new ArrayList<>();
      Office office = lease.getOffice();

      if (imagePaths == null) {
        imagePaths = new ArrayList<>();
      }

      imageList = imagePaths.stream()
          .map(OfficePicture::getFileName)
          .collect(Collectors.toList());

      while (imageList.size() < 5) {
        imageList.add("None");
      }

      return LeaseLookUpServiceResponse.builder()
          .leaseId(lease.getId())
          .name(office.getName())
          .location(office.getOfficeAddress())
          .leaseStatus(lease.getLeaseStatus())
          .paymentDate(lease.getCreatedAt().toLocalDate())
          .startDate(lease.getLeaseStartDate())
          .endDate(lease.getLeaseEndDate())
          .isReviewed(isReviewed)
          .build();
    }
  }
}
