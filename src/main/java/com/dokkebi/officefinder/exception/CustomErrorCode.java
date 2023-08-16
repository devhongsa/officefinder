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
  PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "입력하신 비밀번호가 올바르지 않습니다."),
  OFFICE_OVER_CAPACITY(HttpStatus.BAD_REQUEST, "예약하려는 인원 수가 오피스의 최대 인원 수를 능가합니다."),
  INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST, "오피스 임대를 위한 포인트가 부족합니다."),
  INVALID_OFFICE_ID(HttpStatus.BAD_REQUEST, "아이디에 해당하는 오피스가 존재하지 않습니다.");

  private final HttpStatus httpStatus;
  private final String errorMessage;

}