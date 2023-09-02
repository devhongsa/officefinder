package com.dokkebi.officefinder.service.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.notification.Notification;
import com.dokkebi.officefinder.entity.type.NotificationType;
import com.dokkebi.officefinder.repository.notification.EmitterRepository;
import com.dokkebi.officefinder.repository.notification.NotificationRepository;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private EmitterRepository emitterRepository;

  @Mock
  private NotificationRepository notificationRepository;

  @InjectMocks
  private NotificationService notificationService;

  @Test
  @DisplayName("lastId가 없는 경우")
  public void testSubscribe_WithoutLastEventId() {
    // Given
    String email = "test@example.com";
    String lastEventId = "";

    when(emitterRepository.save(anyString(), any())).thenReturn(new SseEmitter());

    // When
    SseEmitter result = notificationService.subscribe(email, lastEventId);

    // Then
    assertNotNull(result);
  }

  @Test
  @DisplayName("lastId가 있는 경우")
  public void testSubscribe_WithLastEventId() {
    // Given
    String email = "test@example.com";
    String lastEventId = "lastEventId";

    when(emitterRepository.save(anyString(), any())).thenReturn(new SseEmitter());
    when(emitterRepository.findAllEventCacheStartsWithEmail(email)).thenReturn(new HashMap<>());

    SseEmitter result = notificationService.subscribe(email, lastEventId);

    assertNotNull(result);
  }

  @Test
  @DisplayName("고객에게 알림을 보내는 경우")
  public void testSendToCustomer() throws Exception {
    // Given
    Customer mockCustomer = mock(Customer.class);
    when(mockCustomer.getEmail()).thenReturn("test@example.com");

    when(notificationRepository.save(any())).thenReturn(new Notification(
        1L, "Title", "Content", NotificationType.LEASE_DENIED, mockCustomer
    ));

    // When
    notificationService.sendToCustomer(mockCustomer, NotificationType.LEASE_DENIED, "Title",
        "Content");

    // Then
    verify(notificationRepository, times(1)).save(any());
    verify(emitterRepository, times(1)).findAllEmitterStartsWithEmail("test@example.com");
  }

  @Test
  @DisplayName("임대 업자에게 보내는 경우")
  public void testSendToOwner(){
    when(emitterRepository.findAllEmitterStartsWithEmail(anyString())).thenReturn(new HashMap<>());

    notificationService.sendToOwner("test@example.com", NotificationType.LEASE_DENIED, "Title", "Content");

    verify(emitterRepository, times(1)).findAllEmitterStartsWithEmail("test@example.com");
  }
}