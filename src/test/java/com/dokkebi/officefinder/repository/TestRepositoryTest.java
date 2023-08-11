package com.dokkebi.officefinder.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.dokkebi.officefinder.entity.TestEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TestRepositoryTest {

  @Autowired
  private TestRepository testRepository;

  @DisplayName("repository Test")
  @Test
  public void repositoryTest() {
    // given
    TestEntity testEntity = TestEntity.builder()
        .name("test name")
        .build();

    // when
    TestEntity savedTest = testRepository.save(testEntity);
    TestEntity result = testRepository.findById(savedTest.getId()).get();

    // then
    Assertions.assertThat(result.getName()).isEqualTo("test name");
  }

  @DisplayName("repository Test 2")
  @Test
  public void repositoryTest2() {
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