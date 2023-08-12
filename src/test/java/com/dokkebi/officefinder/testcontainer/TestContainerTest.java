package com.dokkebi.officefinder.testcontainer;

import com.dokkebi.officefinder.OfficefinderApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class TestContainerTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @DisplayName("embedded redis test")
  @Test
  public void myTest() {
    redisTemplate.opsForValue().set("key", "key");

    Assertions.assertThat(redisTemplate.opsForValue().get("key")).isEqualTo("key");
  }

  @DisplayName("embedded redis test2")
  @Test
  public void myTest2() {
    // given
    redisTemplate.opsForValue().set("key", "123");

    // when
    redisTemplate.opsForValue().increment("key", 10);
    redisTemplate.opsForValue().increment("key", -20);

    // then
    Assertions.assertThat(redisTemplate.opsForValue().get("key")).isEqualTo("113");
  }

  @DisplayName("embedded redis test3")
  @Test
  public void myTest3() {
    // given
    redisTemplate.opsForValue().set("key", "90");
    redisTemplate.opsForValue().set("key2", "90");

    // when
    redisTemplate.opsForValue().increment("key", 10);
    redisTemplate.opsForValue().increment("key2", 10);

    // then
    Assertions.assertThat(redisTemplate.opsForValue().get("key")).isEqualTo("100");
  }
}