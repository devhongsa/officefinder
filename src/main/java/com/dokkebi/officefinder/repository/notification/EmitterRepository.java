package com.dokkebi.officefinder.repository.notification;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {

  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

  public SseEmitter save(String emitterId, SseEmitter sseEmitter){
    emitters.put(emitterId, sseEmitter);
    return sseEmitter;
  }

  public void saveEventCache(String emitterId, Object event){
    eventCache.put(emitterId, event);
  }

  public Map<String, SseEmitter> findAllEmitterStartsWithEmail(String email){
    return emitters.entrySet().stream()
        .filter(e -> e.getKey().startsWith(email))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Map<String, Object> findAllEventCacheStartsWithEmail(String email){
    return eventCache.entrySet().stream()
        .filter(e -> e.getKey().startsWith(email))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public void deleteById(String emitterId){
    emitters.remove(emitterId);
  }

  public void deleteAllEmitterStartWithEmail(String email){
    Set<String> keysToDelete = emitters.keySet().stream()
        .filter(key -> key.startsWith(email))
        .collect(Collectors.toSet());

    keysToDelete.forEach(emitters::remove);
  }

  public void deleteAllEventCacheStartWithEmail(String email){
    Set<String> keysToDelete = eventCache.keySet().stream()
        .filter(key -> key.startsWith(email))
        .collect(Collectors.toSet());

    keysToDelete.forEach(eventCache::remove);
  }
}
