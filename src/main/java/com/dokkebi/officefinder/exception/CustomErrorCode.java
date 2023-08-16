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

  EMAIL_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "가입되어 있지 않은 이메일입니다."),
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "유저를 찾을 수 없습니다."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "요청 접근 권한이 없습니다."),
  EMAIL_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 등록된 이메일입니다."),
  PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "입력하신 비밀번호가 올바르지 않습니다.");

  private final HttpStatus httpStatus;
  private final String errorMessage;

}