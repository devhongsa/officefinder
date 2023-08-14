package com.dokkebi.officefinder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
  private String status;
  private T data;
}
