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
  INVALID_OFFICE_ID(HttpStatus.BAD_REQUEST, "아이디에 해당하는 오피스가 존재하지 않습니다."),
  FAIL_LOGIN(HttpStatus.UNAUTHORIZED,"로그인에 실패하였습니다. 다시 시도해 주십시오."),
  SSE_SEND_DUMMY_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "더미 데이터 전송에 실패하였습니다."),
  SSE_SEND_MISSED_EVENTS_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이전 이벤트 전송에 실패하였습니다."),
  SSE_SEND_LEASE_NOTIFICATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "임대 알림 전송에 실패하였습니다."),
  OFFICE_NOT_OWNED_BY_OWNER(HttpStatus.NOT_FOUND, "해당 사용자가 소유하고 있는 오피스가 아닙니다."),
  OWNER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 이메일을 가진 임대업자를 찾을 수 없습니다."),
  LEASE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 임대 정보가 조회되지 않습니다."),
  SSE_SEND_ACCEPT_NOTIFICATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "임대 수락 알림 전송에 실패하였습니다."),
  SSE_SEND_REJECT_NOTIFICATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "임대 거절 알림 전송에 실패하였습니다."),
  NO_ROOMS_AVAILABLE_FOR_LEASE(HttpStatus.BAD_REQUEST, "현재 임대예약이 가능한 방이 없습니다.");

  private final HttpStatus httpStatus;
  private final String errorMessage;

}