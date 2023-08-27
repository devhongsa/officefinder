package com.dokkebi.officefinder.repository.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class EmitterRepositoryTest {

  private EmitterRepository repository;
  private SseEmitter emitter;

  @BeforeEach
  void setUp(){
    repository = new EmitterRepository();
    emitter = new SseEmitter();
  }

  @Test
  @DisplayName("Emitter 저장 테스트")
  void saveEmitterTest(){
    //Given
    String email = "test@example.com";

    //When
    repository.save(email, emitter);

    //Then
    assertEquals(emitter, repository.get(email));
  }

  @Test
  void deleteEmitterByEmail(){
    //Given
    String email = "test@example.com";
    repository.save(email, emitter);

    //When
    repository.deleteByEmail(email);

    //Then
    assertNull(repository.get(email));
  }

  @Test
  void getEmitterTest(){
    //Given
    String email = "test@example.com";
    repository.save(email, emitter);

    //When
    SseEmitter retrievedEmitter = repository.get(email);

    //Then
    assertEquals(emitter, retrievedEmitter);
  }
}