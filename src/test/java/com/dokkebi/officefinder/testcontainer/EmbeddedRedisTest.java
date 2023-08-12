package com.dokkebi.officefinder.testcontainer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class EmbeddedRedisTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @DisplayName("embedded redis test2")
  @Test
  public void myRedisTest() {
    // given
    redisTemplate.opsForValue().set("new Key", "123");
    redisTemplate.opsForValue().set("new Key 2", "100");
    redisTemplate.opsForValue().set("new Key 3", "100");

    // when
    redisTemplate.opsForValue().increment("new Key", 15);
    redisTemplate.opsForValue().increment("new Key 2", -10);
    redisTemplate.opsForValue().increment("new Key 3", -50);

    // then
    Assertions.assertThat(redisTemplate.opsForValue().get("new Key")).isEqualTo("138");
    Assertions.assertThat(redisTemplate.opsForValue().get("new Key 2")).isEqualTo("90");
    Assertions.assertThat(redisTemplate.opsForValue().get("new Key 3")).isEqualTo("50");
  }
}