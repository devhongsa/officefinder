package com.dokkebi.officefinder.entity.type;

/**
 * AWAIT : 주문 시도 후, 임대 업자의 승인 받기 전
 * ACCEPTED : 임대 업자의 승인을 받아, 임대가 정상 접수된 상태
 * DENIED : 임대 업자가 임대를 거절한 경우
 * PROCEEDING : 오피스를 사용중인 상태
 * CANCELED : 사용자에 의해 임대가 취소된 상태
 * EXPIRED : 임대 사용이 끝났을 경우
 */
public enum LeaseStatus {
  AWAIT, ACCEPTED, DENIED,
  PROCEEDING, CANCELED, EXPIRED
}
