package com.dokkebi.officefinder.controller.officeowner.dto;

import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficePicture;
import java.util.List;
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
  private String imagePath;

  public static OwnerOfficeOverViewDto fromEntity(Office office, List<OfficePicture> imagePathList) {
    String imagePath;

    if (imagePathList == null || imagePathList.isEmpty()){
      imagePath = "None";
    } else{
      imagePath = imagePathList.get(0).getFileName();
    }

    return OwnerOfficeOverViewDto.builder()
        .id(office.getId())
        .officeName(office.getName())
        .address(office.getOfficeAddress())
        .imagePath(imagePath)
        .build();
  }
}
