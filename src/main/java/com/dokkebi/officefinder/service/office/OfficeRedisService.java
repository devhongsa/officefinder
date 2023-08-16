package com.dokkebi.officefinder.service.office;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfficeRedisService {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String REMAIN_ROOM_KEY = "remain-room";

  /*
  남은 방 설정 - 만약 현재 입주한 사람 수보다 더 적게 설정해버린다면 예외가 발생하도록 구현해야 한다.
   */
  public void setRemainRoom(String officeName, int remainRoom) {
    redisTemplate.opsForHash().put(REMAIN_ROOM_KEY, officeName, String.valueOf(remainRoom));
  }

  /*
  남은 방 감소 - 예약 시 남은 방이 한 예약 당 1씩 감소해야 한다. 0보다 더 작을 수는 없다.
   */
  public void decreaseRemainRoom(String officeName) {
    Object o = redisTemplate.opsForHash().get(REMAIN_ROOM_KEY, officeName);

    if (o == null) {
      throw new IllegalArgumentException("해당 오피스가 존재하지 않습니다.");
    }

    if (Integer.parseInt(String.valueOf(o)) == 0) {
      throw new IllegalArgumentException("해당 오피스에 빈 사무실이 없습니다.");
    }

    redisTemplate.opsForHash().increment(REMAIN_ROOM_KEY, officeName, -1);
  }

  /*
  남은 방 증가 - 예약 만료 시 남은 방은 1씩 증가해야 한다.
   */
  public void increaseRemainRoom(String officeName) {
    Object o = redisTemplate.opsForHash().get(REMAIN_ROOM_KEY, officeName);

    if (o == null) {
      throw new IllegalArgumentException("해당 오피스가 존재하지 않습니다.");
    }

    redisTemplate.opsForHash().increment(REMAIN_ROOM_KEY, officeName, 1);
  }

  public String getRemainRoom(String officeName) {
    Object o = redisTemplate.opsForHash().get(REMAIN_ROOM_KEY, officeName);

    if (o == null) {
      throw new IllegalArgumentException("해당 오피스가 존재하지 않습니다.");
    }

    return String.valueOf(o);
  }
}
