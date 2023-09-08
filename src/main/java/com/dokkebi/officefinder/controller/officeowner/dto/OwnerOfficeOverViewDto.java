package com.dokkebi.officefinder.controller.officeowner.dto;

import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficePicture;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OwnerOfficeOverViewDto {

  private Long id;
  private String officeName;
  private String address;
  private List<String> imagePath;

  public static OwnerOfficeOverViewDto fromEntity(Office office,
      List<OfficePicture> imagePathList) {
    List<String> imagePathData = new ArrayList<>();

    if (imagePathList == null || imagePathList.isEmpty()) {
      for (int i = 0; i < 5; i++) {
        imagePathData.add("None");
      }
    } else {
      imagePathData = imagePathList.stream()
          .map(OfficePicture::getFileName)
          .collect(Collectors.toList());
    }

    return OwnerOfficeOverViewDto.builder()
        .id(office.getId())
        .officeName(office.getName())
        .address(office.getOfficeAddress())
        .imagePath(imagePathData)
        .build();
  }
}
