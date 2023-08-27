package com.dokkebi.officefinder.controller.bookmark.dto;

import com.dokkebi.officefinder.entity.bookmark.Bookmark;
import com.dokkebi.officefinder.entity.office.Office;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkDto {
  String officeName;
  String officeAddress;

  public static BookmarkDto from(Bookmark bookmark) {
    Office office = bookmark.getOffice();
    return BookmarkDto.builder()
        .officeName(office.getName())
        .officeAddress(office.getOfficeAddress())
        .build();
  }
}