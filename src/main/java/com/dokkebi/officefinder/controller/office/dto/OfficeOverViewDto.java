package com.dokkebi.officefinder.controller.office.dto;

import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.service.review.dto.ReviewOverviewDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OfficeOverViewDto {

  private Long id;
  private String name;
  private String location;
  private int reviewCount;
  private Double reviewRate;
  private long leasePrice;
  private String imagePath;

  @Builder
  private OfficeOverViewDto(Long id, String name, String location, int reviewCount,
      Double reviewRate, long leasePrice, String imagePath) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.reviewCount = reviewCount;
    this.reviewRate = reviewRate;
    this.leasePrice = leasePrice;
    this.imagePath = imagePath;
  }

  public static OfficeOverViewDto fromEntity(Office office, ReviewOverviewDto reviewOverview,
      String image) {
    String address = office.getOfficeLocation().getAddress().getLegion() + " "
        + office.getOfficeLocation().getAddress().getCity();

    return OfficeOverViewDto.builder()
        .id(office.getId())
        .location(address)
        .name(office.getName())
        .leasePrice(office.getLeaseFee())
        .reviewCount(reviewOverview.getReviewCount())
        .reviewRate(reviewOverview.getReviewRate())
        .imagePath(image)
        .build();
  }
}