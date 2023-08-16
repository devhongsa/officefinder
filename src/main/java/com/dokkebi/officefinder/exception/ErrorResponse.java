package com.dokkebi.officefinder.exception;

import lombok.Builder;
import lombok.Getter;

/*
    커스텀 예외 발생시 서버 응답 Dto
 */
@Getter
@Builder
public class ErrorResponse {
  private String status;
  private int statusCode;
  private CustomErrorCode errorCode;
  private String errorMessage;
}
