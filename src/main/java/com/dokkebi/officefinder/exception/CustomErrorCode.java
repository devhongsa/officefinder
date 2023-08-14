package com.dokkebi.officefinder.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/*
    커스텀 exception
 */
@Getter
@AllArgsConstructor
public enum CustomErrorCode {

  USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST ,"이미 회원가입이 되어있는 아이디입니다."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "요청 접근 권한이 없습니다.");

  private final HttpStatus httpStatus;
  private final String errorMessage;

}