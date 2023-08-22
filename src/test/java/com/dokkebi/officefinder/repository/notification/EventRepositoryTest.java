package com.dokkebi.officefinder.repository.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dokkebi.officefinder.dto.Event;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventRepositoryTest {

  private EventRepository eventRepository;

  @BeforeEach
  void setup(){
    eventRepository = new EventRepository();
  }

  @Test
  @DisplayName("이벤트 저장시 아이디 증가(1,2 ...) 테스트")
  void addEvent_IncreaseIdTest(){
    // Given
    String email = "test@example.com";
    Event event1 = new Event();
    Event event2 = new Event();

    // When
    eventRepository.addEvent(email, event1);
    eventRepository.addEvent(email, event2);

    // Then
    assertEquals(1, event1.getEventId());
    assertEquals(2, event2.getEventId());
  }

  @Test
  @DisplayName("Last-Event-ID 이후에 Event들을 클라이언트에게 전달하는지 테스트")
  void getMissedEvents_shouldReturnEventsAfterGivenId(){
    // Given
    String email = "test@example.com";
    Event event1 = new Event();
    Event event2 = new Event();
    eventRepository.addEvent(email, event1);
    eventRepository.addEvent(email, event2);

    // When
    List<Event> missedEvents = eventRepository.getMissedEvents(email, 1);

    // Then
    assertFalse(missedEvents.contains(event1));
    assertTrue(missedEvents.contains(event2));
  }


}