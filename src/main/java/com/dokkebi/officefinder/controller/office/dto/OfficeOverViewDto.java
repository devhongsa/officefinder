package com.dokkebi.officefinder.controller.office.dto;

import com.dokkebi.officefinder.entity.office.Office;
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

  @Builder
  private OfficeOverViewDto(Long id, String name, String location, int reviewCount,
      Double reviewRate, long leasePrice) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.reviewCount = reviewCount;
    this.reviewRate = reviewRate;
    this.leasePrice = leasePrice;
  }

  public static OfficeOverViewDto fromEntity(Office office, int reviewCount, double reviewRate) {
    StringBuilder addressBuilder = new StringBuilder();
    addressBuilder.append(office.getOfficeLocation().getAddress().getLegion()).append(" ");
    addressBuilder.append(office.getOfficeLocation().getAddress().getCity());

    String address = addressBuilder.toString();

    return OfficeOverViewDto.builder()
        .id(office.getId())
        .location(address)
        .name(office.getName())
        .leasePrice(office.getLeaseFee())
        .reviewCount(reviewCount)
        .reviewRate(reviewRate)
        .build();
  }
}
