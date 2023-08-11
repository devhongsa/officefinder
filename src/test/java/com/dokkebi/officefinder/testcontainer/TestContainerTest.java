package com.dokkebi.officefinder.testcontainer;

import com.dokkebi.officefinder.TestContainerConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
@ExtendWith(TestContainerConfig.class)
public class TestContainerTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @DisplayName("container test")
  @Test
  public void myTest() {

  }
}
