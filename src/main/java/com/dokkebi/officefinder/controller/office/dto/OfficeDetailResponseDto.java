package com.dokkebi.officefinder.controller.office.dto;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.ReviewDto;
import com.dokkebi.officefinder.entity.office.Office;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficeDetailResponseDto {

  private String officeName;
  private String address;
  private int maxRoomCount;
  private long leaseFee;
  private int maxCapacity;
  private String reviewCount;
  private OfficeOptionDto officeOptionDto;
  private List<ReviewDto> reviews;
  private List<String> officePictureList;

  public static OfficeDetailResponseDto from(Office office,
      List<ReviewDto> reviewDtoList, List<String> imagePaths) {

    return OfficeDetailResponseDto.builder()
        .officeName(office.getName())
        .leaseFee(office.getLeaseFee())
        .maxCapacity(office.getMaxCapacity())
        .address(office.getOfficeAddress())
        .maxRoomCount(office.getMaxRoomCount())
        .officeOptionDto(OfficeOptionDto.fromEntity(office.getOfficeCondition()))
        .reviews(reviewDtoList)
        .reviewCount(String.valueOf(office.getReviewCount()))
        .officePictureList(imagePaths)
        .build();
  }
}