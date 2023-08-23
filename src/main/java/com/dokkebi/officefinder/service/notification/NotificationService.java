package com.dokkebi.officefinder.service.notification;

import com.dokkebi.officefinder.dto.Event;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.notification.EmitterRepository;
import com.dokkebi.officefinder.repository.notification.EventRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

  private final EmitterRepository emitterRepository;

  private final EventRepository eventRepository;

  // 회원의 email을 바탕으로 SSE 연결을 설정
  // LastEvenId가 포함된 경우, 연결이 끊긴 이후의 Event들을 전송
  public SseEmitter subscribe(String email, String lastEventId){
    SseEmitter emitter = createEmitter(email);

    if(!lastEventId.isEmpty()){
      sendAfterEvents(emitter, email, lastEventId);
    }else{
      sendDummyData(emitter);
    }

    return emitter;
  }

  // 임대 알림을 전송하는 메서드
  public void sendLeaseNotification(Office office){
    String email = office.getOwner().getEmail();
    String officeName = office.getName();
    String message = officeName + " 임대 요청이 들어왔습니다.";

    Event event = new Event();
    event.setEventType("leaseNotification");
    event.setEventData(message);

    eventRepository.addEvent(email, event);

    SseEmitter sseEmitter = emitterRepository.get(email);

    if(sseEmitter != null){
      try{
        sseEmitter.send(SseEmitter.event().name(event.getEventType()).data(event.getEventData()));
      }catch(IOException e){
        throw new CustomException(CustomErrorCode.SSE_SEND_LEASE_NOTIFICATION_FAIL);
      }
    }
  }

  // 첫 연결 시 더미 데이터를 전송하는 메서드
  // 이벤트가 전송되지 않으면 연결 요청에 오류가 발생하거나 재연결 요청이 발생할 수 있기 때문
  private void sendDummyData(SseEmitter emitter) {
    try{
      emitter.send(SseEmitter.event().name("connect"));
    }catch(IOException e){
      throw new CustomException(CustomErrorCode.SSE_SEND_DUMMY_FAIL);
    }
  }

  // 주어진 lastEventId 이후의 모든 이벤트를 전송하는 메서드
  private void sendAfterEvents(SseEmitter emitter, String email, String lastEventId) {
    int eventId = Integer.parseInt(lastEventId);
    List<Event> missedEvents = eventRepository.getMissedEvents(email, eventId);

    for(Event event : missedEvents){
      try{
        emitter.send(SseEmitter.event().name(event.getEventType()).data(event.getEventData()));
      }catch(IOException e){
        throw new CustomException(CustomErrorCode.SSE_SEND_MISSED_EVENTS_FAIL);
      }
    }
  }

  private SseEmitter createEmitter(String email) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
    emitterRepository.save(email, emitter);

    emitter.onCompletion(() -> emitterRepository.deleteByEmail(email));
    emitter.onTimeout(() -> emitterRepository.deleteByEmail(email));

    return emitter;
  }
}
