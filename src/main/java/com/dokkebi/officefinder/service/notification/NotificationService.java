package com.dokkebi.officefinder.service.notification;

import static com.dokkebi.officefinder.entity.notification.CustomerNotification.createCustomerNotification;
import static com.dokkebi.officefinder.entity.notification.OfficeOwnerNotification.createOwnerNotification;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.notification.CustomerNotification;
import com.dokkebi.officefinder.entity.notification.OfficeOwnerNotification;
import com.dokkebi.officefinder.entity.type.NotificationType;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.notification.EmitterRepository;
import com.dokkebi.officefinder.repository.notification.CustomerNotificationRepository;
import com.dokkebi.officefinder.repository.notification.OfficeOwnerNotificationRepository;
import com.dokkebi.officefinder.service.notification.dto.NotificationResponseDto;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final OfficeOwnerRepository officeOwnerRepository;

  private final CustomerRepository customerRepository;

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

  private final EmitterRepository emitterRepository;

  private final CustomerNotificationRepository customerNotificationRepository;

  private final OfficeOwnerNotificationRepository officeOwnerNotificationRepository;

  // 회원의 email을 바탕으로 SSE 연결을 설정
  // LastEvenId가 포함된 경우, 연결이 끊긴 이후의 Event들을 전송
  public SseEmitter subscribe(String email, String lastEventId) {
    String emitterId = email + "_" + System.currentTimeMillis();
    SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

    emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
    emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

    // 더미 데이터 전송
    sendToClient(emitter, emitterId, "연결이 성공하였습니다. [email :" + email + "]");

    // 연결이 끊긴 사이에 전송된 알림들 전송
    if (!lastEventId.isEmpty()) {
      Map<String, Object> events = emitterRepository.findAllEventCacheStartsWithEmail(email);
      events.entrySet().stream()
          .filter(e -> lastEventId.compareTo(e.getKey()) < 0)
          .forEach(e -> sendToClient(emitter, e.getKey(), e.getValue()));
    }

    return emitter;
  }

  public void sendToCustomer(Customer customer, NotificationType notificationType, String title,
      String content) {
    CustomerNotification notification = customerNotificationRepository.save(
        createCustomerNotification(customer, notificationType, title, content));
    sendNotificationToEmail(customer.getEmail(), NotificationResponseDto.from(notification));
  }

  public void sendToOwner(OfficeOwner officeOwner, NotificationType notificationType, String title,
      String content) {
    OfficeOwnerNotification notification = officeOwnerNotificationRepository.save(
        createOwnerNotification(officeOwner, notificationType, title, content));
    sendNotificationToEmail(officeOwner.getEmail(), NotificationResponseDto.from(notification));
  }

  public Page<NotificationResponseDto> getNotificationByOwner(String email, Pageable pageable){

    OfficeOwner officeOwner = officeOwnerRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(CustomErrorCode.OWNER_NOT_FOUND));

    Page<OfficeOwnerNotification> notifications = officeOwnerNotificationRepository.findAllByOfficeOwner(
        officeOwner, pageable);

    return notifications.map(NotificationResponseDto::from);
  }

  public Page<NotificationResponseDto> getNotificationByCustomer(String email, Pageable pageable){

    Customer customer = customerRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

    Page<CustomerNotification> notifications = customerNotificationRepository.findAllByCustomer(
        customer, pageable);

    return notifications.map(NotificationResponseDto::from);
  }

  private void sendNotificationToEmail(String email, Object notificationData) {
    Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartsWithEmail(email);

    sseEmitters.forEach((key, emitter) -> {
      emitterRepository.saveEventCache(key, notificationData);
      sendToClient(emitter, key, notificationData);
    });
  }

  private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
    try {
      emitter.send(SseEmitter.event()
          .id(emitterId)
          .data(data));
    } catch (IOException exception) {
      emitterRepository.deleteById(emitterId);
      exception.printStackTrace();
      throw new CustomException(CustomErrorCode.SSE_SEND_NOTIFICATION_FAIL);
    }
  }
}
