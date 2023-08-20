package com.dokkebi.officefinder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageResponseDto<T> {

  T data;
  PageInfo pageInfo;
}
