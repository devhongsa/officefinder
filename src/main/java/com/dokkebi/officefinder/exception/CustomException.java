package com.dokkebi.officefinder.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends RuntimeException{

  private CustomErrorCode errorCode;
  private String errorMessage;
  private HttpStatus status;

  public CustomException(CustomErrorCode errorCode){
    this.errorCode = errorCode;
    this.errorMessage = errorCode.getErrorMessage();
    this.status = errorCode.getHttpStatus();
  }
}

