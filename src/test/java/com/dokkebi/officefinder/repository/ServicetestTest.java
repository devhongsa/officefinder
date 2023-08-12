package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.TestEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ServicetestTest {


  @Autowired
  private TestRepository testRepository;

  @DisplayName("Service test 1")
  @Test
  public void ServiceTest() {
    // given
    TestEntity testEntity = TestEntity.builder()
        .name("test name")
        .build();

    // when
    TestEntity savedTest = testRepository.save(testEntity);
    testRepository.delete(savedTest);

    // then
    Assertions.assertThat(testRepository.existsById(savedTest.getId())).isFalse();
  }

  @DisplayName("Service test 2")
  @Test
  public void ServiceTest2() {
    // given
    TestEntity testEntity = TestEntity.builder()
        .name("test name")
        .build();

    // when
    TestEntity savedTest = testRepository.save(testEntity);
    testRepository.delete(savedTest);

    // then
    Assertions.assertThat(testRepository.existsById(savedTest.getId())).isFalse();
  }
}
