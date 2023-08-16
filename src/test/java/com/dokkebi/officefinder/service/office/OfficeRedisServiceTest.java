package com.dokkebi.officefinder.service.office;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OfficeRedisServiceTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  @Autowired
  private OfficeRedisService officeRedisService;

  private static final String REMAIN_ROOM_KEY = "remain-room";

  @AfterEach
  void tearDown() {
    redisTemplate.delete(REMAIN_ROOM_KEY);
  }

  @DisplayName("지정된 수 만큼 Redis 에 남은 방의 수를 저장할 수 있다.")
  @Test
  public void setRemainRoomTest() {
    // given
    int remainRoom = 10;
    String officeName = "office1";

    // when
    officeRedisService.setRemainRoom(officeName, remainRoom);

    // then
    Object o = redisTemplate.opsForHash().get(REMAIN_ROOM_KEY, officeName);

    assertThat(o).isNotNull();
    assertThat(Integer.parseInt(String.valueOf(o))).isEqualTo(10);
  }

  @DisplayName("남은 방의 수를 감소시킬 수 있다.")
  @Test
  public void decreaseRemainRoomTest() {
    // given
    int remainRoom = 10;
    String officeName = "office1";

    officeRedisService.setRemainRoom(officeName, remainRoom);

    // when
    officeRedisService.decreaseRemainRoom(officeName);

    // then
    Object o = redisTemplate.opsForHash().get(REMAIN_ROOM_KEY, officeName);

    assertThat(o).isNotNull();
    assertThat(Integer.parseInt(String.valueOf(o))).isEqualTo(9);
  }

  @DisplayName("존재하지 않는 오피스의 남은 방을 감소시킬 수 없다.")
  @Test
  public void decreaseRemainRoomTestWithWrongOfficeName() {
    // given
    int remainRoom = 10;
    String officeName = "office1";

    officeRedisService.setRemainRoom(officeName, remainRoom);

    // when
    // then
    assertThatThrownBy(() -> officeRedisService.decreaseRemainRoom("office2"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당 오피스가 존재하지 않습니다.");
  }

  @DisplayName("남은 방이 없으면 더 이상 줄일 수 없다.")
  @Test
  public void decreaseRemainRoomTestWithNoRemainRoom() {
    // given
    int remainRoom = 1;
    String officeName = "office1";

    officeRedisService.setRemainRoom(officeName, remainRoom);
    officeRedisService.decreaseRemainRoom(officeName);

    // when
    // then
    assertThatThrownBy(() -> officeRedisService.decreaseRemainRoom(officeName))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당 오피스에 빈 사무실이 없습니다.");
  }

  @DisplayName("남은 방의 수를 늘릴 수 있다.")
  @Test
  public void increaseRemainRoomTest() {
    // given
    int remainRoom = 9;
    String officeName = "office1";

    officeRedisService.setRemainRoom(officeName, remainRoom);

    // when
    officeRedisService.increaseRemainRoom(officeName);

    // then
    Object o = redisTemplate.opsForHash().get(REMAIN_ROOM_KEY, officeName);

    assertThat(o).isNotNull();
    assertThat(Integer.parseInt(String.valueOf(o))).isEqualTo(10);
  }

  @DisplayName("존재하지 않는 오피스의 남은 방 수를 늘리려고 할 시 예외가 발생한다.")
  @Test
  public void increaseRemainRoomTestWithWrongOfficeName() {
    // given
    int remainRoom = 9;
    String officeName = "office1";

    officeRedisService.setRemainRoom(officeName, remainRoom);

    // when
    // then
    assertThatThrownBy(() -> officeRedisService.increaseRemainRoom("office2"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당 오피스가 존재하지 않습니다.");
  }
}