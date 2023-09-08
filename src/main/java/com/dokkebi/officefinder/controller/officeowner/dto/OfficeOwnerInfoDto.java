package com.dokkebi.officefinder.controller.officeowner.dto;

import com.dokkebi.officefinder.entity.OfficeOwner;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OfficeOwnerInfoDto {

  private Long id;
  private String email;
  private String name;
  private long point;
  private Set<String> roles;
  private String profileImagePath;

  public static OfficeOwnerInfoDto from(OfficeOwner officeOwner) {
    return OfficeOwnerInfoDto.builder()
        .id(officeOwner.getId())
        .email(officeOwner.getEmail())
        .name(officeOwner.getName())
        .point(officeOwner.getPoint())
        .roles(officeOwner.getRoles())
        .profileImagePath(officeOwner.getOfficeOwnerProfileImage())
        .build();
  }
}
