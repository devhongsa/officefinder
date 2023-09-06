package com.dokkebi.officefinder.controller.officeowner.dto;

import com.dokkebi.officefinder.entity.OfficeOwner;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OfficeOwnerOverViewDto {

  private Long id;
  private String name;
  private long point;
  private Set<String> roles;
  private String profileImagePath;

  public static OfficeOwnerOverViewDto from(OfficeOwner officeOwner) {
    return OfficeOwnerOverViewDto.builder()
        .id(officeOwner.getId())
        .name(officeOwner.getName())
        .point(officeOwner.getPoint())
        .roles(officeOwner.getRoles())
        .profileImagePath(officeOwner.getOfficeOwnerProfileImage())
        .build();
  }
}
