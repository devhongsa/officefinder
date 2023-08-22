package com.dokkebi.officefinder.repository.notification;

import com.dokkebi.officefinder.dto.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class EventRepository {

  private final Map<String, List<Event>> userEvents = new ConcurrentHashMap<>();

  private AtomicInteger globalEventId = new AtomicInteger(0);

  public void addEvent(String email, Event event){
    event.setEventId(globalEventId.incrementAndGet());
    userEvents.computeIfAbsent(email, e -> new ArrayList<>()).add(event);
  }

  public List<Event> getMissedEvents(String email, int lastEventId){
    return userEvents.getOrDefault(email, new ArrayList<>())
        .stream()
        .filter(event -> event.getEventId() > lastEventId)
        .collect(Collectors.toList());
  }
}
