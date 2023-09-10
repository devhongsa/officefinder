package com.dokkebi.officefinder.controller.office.dto;

import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficePicture;
import com.dokkebi.officefinder.service.review.dto.ReviewOverviewDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
  private long reviewCount;
  private long reviewRate;
  private long leasePrice;
  private List<String> imagePath;

  @Builder
  private OfficeOverViewDto(Long id, String name, String location, long reviewCount,
      long reviewRate, long leasePrice, List<String> imagePath) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.reviewCount = reviewCount;
    this.reviewRate = reviewRate;
    this.leasePrice = leasePrice;
    this.imagePath = imagePath;
  }

  public static OfficeOverViewDto fromEntity(Office office, List<OfficePicture> officeImages) {

    String address = office.getOfficeLocation().getAddress().getLegion() + " "
        + office.getOfficeLocation().getAddress().getCity();

    List<String> imagePathList = new ArrayList<>();
    imagePathList = officeImages.stream()
        .map(OfficePicture::getFileName)
        .collect(Collectors.toList());

    while(imagePathList.size() < 5){
      imagePathList.add("None");
    }

    return OfficeOverViewDto.builder()
        .id(office.getId())
        .location(address)
        .name(office.getName())
        .leasePrice(office.getLeaseFee())
        .reviewCount(office.getReviewCount())
        .reviewRate(office.getTotalRate())
        .imagePath(imagePathList)
        .build();
  }
}