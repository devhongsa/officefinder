package com.dokkebi.officefinder.testcontainer;

import com.dokkebi.officefinder.OfficefinderApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(classes = OfficefinderApplication.class)
public class TestContainerTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @DisplayName("container test")
  @Test
  public void myTest() {
    redisTemplate.opsForValue().set("key", "key");

    Assertions.assertThat(redisTemplate.opsForValue().get("key")).isEqualTo("key");
  }
}
