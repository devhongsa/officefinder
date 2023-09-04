package com.dokkebi.officefinder.repository.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class EmitterRepositoryTest {

  private EmitterRepository repository;
  private SseEmitter emitter;

  @BeforeEach
  void setUp() {
    repository = new EmitterRepository();
    emitter = new SseEmitter();
  }

  @Test
  @DisplayName("Emitter 저장 테스트")
  void saveEmitterTest() {
    //Given
    String emitterId = "test@example.com" + "_" + System.currentTimeMillis();

    //When
    SseEmitter savedSseEmitter = repository.save(emitterId, emitter);

    //Then
    assertEquals(emitter, savedSseEmitter);
  }

  @Test
  @DisplayName("EventCache 저장 테스트")
  void saveEventCacheTest() {
    // Given
    String emitterId = "test@example.com" + "_" + System.currentTimeMillis();
    String testEvent = "Test Event";

    // When
    repository.saveEventCache(emitterId, testEvent);

    // Then
    Map<String, Object> events = repository.findAllEventCacheStartsWithEmail("test@example.com");
    assertTrue(events.containsValue(testEvent));
  }

  @Test
  @DisplayName("Emitter 삭제 테스트")
  void deleteByIdTest() {
    // Given
    String emitterId = "test@example.com" + "_" + System.currentTimeMillis();
    repository.save(emitterId, emitter);

    // When
    repository.deleteById(emitterId);

    // Then
    Map<String, SseEmitter> emitters = repository.findAllEmitterStartsWithEmail("test@example.com");
    assertFalse(emitters.containsKey(emitterId));
  }

  @Test
  @DisplayName("Emitter 검색 테스트")
  void findAllEmitterStartsWithEmailTest() {
    // Given
    String emitterId1 = "test@example.com" + "_" + System.currentTimeMillis();
    String emitterId2 = "test@example.com" + "_" + (System.currentTimeMillis() + 1);
    String emitterId3 = "different@example.com" + "_" + System.currentTimeMillis();
    repository.save(emitterId1, emitter);
    repository.save(emitterId2, emitter);
    repository.save(emitterId3, emitter);

    // When
    Map<String, SseEmitter> emitters = repository.findAllEmitterStartsWithEmail("test@example.com");

    // Then
    assertEquals(2, emitters.size());
    assertTrue(emitters.containsKey(emitterId1));
    assertTrue(emitters.containsKey(emitterId2));
    assertFalse(emitters.containsKey(emitterId3));
  }

  @Test
  @DisplayName("EventCache 검색 테스트")
  void findAllEventCacheStartsWithEmailTest() {
    // Given
    String emitterId1 = "test@example.com" + "_" + System.currentTimeMillis();
    String emitterId2 = "test@example.com" + "_" + (System.currentTimeMillis() + 1);
    String emitterId3 = "different@example.com" + "_" + System.currentTimeMillis();
    String event1 = "Event 1";
    String event2 = "Event 2";
    String event3 = "Event 3";
    repository.saveEventCache(emitterId1, event1);
    repository.saveEventCache(emitterId2, event2);
    repository.saveEventCache(emitterId3, event3);

    // When
    Map<String, Object> events = repository.findAllEventCacheStartsWithEmail("test@example.com");

    // Then
    assertEquals(2, events.size());
    assertTrue(events.containsValue(event1));
    assertTrue(events.containsValue(event2));
    assertFalse(events.containsValue(event3));
  }

  @Test
  @DisplayName("Emitter 전체 삭제 테스트")
  void deleteAllEmitterStartWithEmailTest() {
    // Given
    String emitterId1 = "test@example.com" + "_" + System.currentTimeMillis();
    String emitterId2 = "test@example.com" + "_" + (System.currentTimeMillis() + 1);
    repository.save(emitterId1, emitter);
    repository.save(emitterId2, emitter);

    // When
    repository.deleteAllEmitterStartWithEmail("test@example.com");

    // Then
    Map<String, SseEmitter> emitters = repository.findAllEmitterStartsWithEmail("test@example.com");
    assertTrue(emitters.isEmpty());
  }

  @Test
  @DisplayName("EventCache 전체 삭제 테스트")
  void deleteAllEventCacheStartWithEmailTest() {
    // Given
    String emitterId1 = "test@example.com" + "_" + System.currentTimeMillis();
    String emitterId2 = "test@example.com" + "_" + (System.currentTimeMillis() + 1);
    repository.saveEventCache(emitterId1, "Event 1");
    repository.saveEventCache(emitterId2, "Event 2");

    // When
    repository.deleteAllEventCacheStartWithEmail("test@example.com");

    // Then
    Map<String, Object> events = repository.findAllEventCacheStartsWithEmail("test@example.com");
    assertTrue(events.isEmpty());
  }
}