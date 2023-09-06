package com.dokkebi.officefinder.controller.office.dto;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.ReviewDto;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficePicture;
import com.dokkebi.officefinder.entity.review.Review;
import java.util.List;
import java.util.stream.Collectors;
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
  private OfficeOptionDto officeOptionDto;
  private List<ReviewDto> reviews;
  private List<String> officePictureList;

  public static OfficeDetailResponseDto from(Office office, List<Review> reviews,
      List<OfficePicture> pictures) {

    List<ReviewDto> dtoList = reviews.stream()
        .map(ReviewDto::from)
        .collect(Collectors.toList());

    List<String> picturePath = pictures.stream()
        .map(OfficePicture::getFileName)
        .collect(Collectors.toList());

    return OfficeDetailResponseDto.builder()
        .officeName(office.getName())
        .leaseFee(office.getLeaseFee())
        .maxCapacity(office.getMaxCapacity())
        .address(office.getOfficeAddress())
        .maxRoomCount(office.getMaxRoomCount())
        .officeOptionDto(OfficeOptionDto.fromEntity(office.getOfficeCondition()))
        .reviews(dtoList)
        .officePictureList(picturePath)
        .build();
  }
}