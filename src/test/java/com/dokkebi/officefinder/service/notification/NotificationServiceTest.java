package com.dokkebi.officefinder.service.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dokkebi.officefinder.dto.Event;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.repository.notification.EmitterRepository;
import com.dokkebi.officefinder.repository.notification.EventRepository;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private EmitterRepository emitterRepository;

  @Mock
  private EventRepository eventRepository;

  @InjectMocks
  private NotificationService notificationService;

  @Test
  @DisplayName("lastId가 있는 경우, 전달되지 않았던 이벤트 전송")
  public void testSubscribe_WithLastEventId() {
    // Given
    String email = "test@example.com";
    String lastEventId = "1";

    when(eventRepository.getMissedEvents(email, Integer.parseInt(lastEventId)))
        .thenReturn(Arrays.asList(new Event(), new Event()));

    // When
    SseEmitter returnedEmitter = notificationService.subscribe(email, lastEventId);

    // Then
    assertNotNull(returnedEmitter);
    verify(eventRepository).getMissedEvents(email, Integer.parseInt(lastEventId));
  }

  @Test
  @DisplayName("lastId가 없는 경우, 더미 데이터 전송")
  public void testSubscribe_WithoutLastEventId() {
    // Given
    String email = "test@example.com";
    String lastEventId = "";

    // When
    SseEmitter returnedEmitter = notificationService.subscribe(email, lastEventId);

    // Then
    assertNotNull(returnedEmitter);
    verify(emitterRepository).save(eq(email), any());
  }

  @Test
  @DisplayName("임대 알림을 보내는 경우")
  public void testSendLeaseNotification() throws Exception{
    // Given
    Office mockOffice = mock(Office.class);
    OfficeOwner mockOwner = mock(OfficeOwner.class);

    String email = "test@example.com";
    String officeName = "testOffice";
    String expectedMessage = officeName + " 임대 요청이 들어왔습니다.";

    when(mockOffice.getOwner()).thenReturn(mockOwner);
    when(mockOwner.getEmail()).thenReturn(email);
    when(mockOffice.getName()).thenReturn(officeName);

    SseEmitter mockEmitter = mock(SseEmitter.class);
    when(emitterRepository.get(email)).thenReturn(mockEmitter);

    // When
    notificationService.sendLeaseNotification(mockOffice);

    // Then
    verify(emitterRepository).get(email);
    verify(mockEmitter).send(any(SseEmitter.SseEventBuilder.class));
  }

  @Test
  @DisplayName("임대 수락 알림을 보내는 경우")
  public void testSendAcceptNotification() throws Exception{
    // Given
    Lease mockLease = mock(Lease.class);
    Office mockOffice = mock(Office.class);
    Customer mockCustomer = mock(Customer.class);

    String email = "test@example.com";
    String officeName = "testOffice";
    String expectedMessage = officeName + "에 대한 임대 요청이 수락되었습니다 :)";

    when(mockLease.getOffice()).thenReturn(mockOffice);
    when(mockLease.getCustomer()).thenReturn(mockCustomer);
    when(mockOffice.getName()).thenReturn(officeName);
    when(mockCustomer.getEmail()).thenReturn(email);

    SseEmitter mockEmitter = mock(SseEmitter.class);
    when(emitterRepository.get(email)).thenReturn(mockEmitter);

    // When
    notificationService.sendAcceptNotification(mockLease);

    // Then
    verify(emitterRepository).get(email);
    verify(mockEmitter).send(any(SseEmitter.SseEventBuilder.class));
  }
}