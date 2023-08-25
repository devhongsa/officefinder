package com.dokkebi.officefinder.controller.bookmark.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkDto {
  String name;
  String officeAddress;
}