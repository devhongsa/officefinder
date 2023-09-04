package com.dokkebi.officefinder.service.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.notification.CustomerNotification;
import com.dokkebi.officefinder.entity.notification.OfficeOwnerNotification;
import com.dokkebi.officefinder.entity.type.NotificationType;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.notification.EmitterRepository;
import com.dokkebi.officefinder.repository.notification.CustomerNotificationRepository;
import com.dokkebi.officefinder.repository.notification.OfficeOwnerNotificationRepository;
import com.dokkebi.officefinder.service.notification.dto.NotificationResponseDto;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private EmitterRepository emitterRepository;

  @Mock
  private CustomerNotificationRepository customerNotificationRepository;

  @Mock
  private OfficeOwnerNotificationRepository officeOwnerNotificationRepository;

  @Mock
  private OfficeOwnerRepository officeOwnerRepository;

  @Mock
  private CustomerRepository customerRepository;

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

    when(customerNotificationRepository.save(any())).thenReturn(new CustomerNotification(
        1L, "Title", "Content", NotificationType.LEASE_DENIED, mockCustomer
    ));

    // When
    notificationService.sendToCustomer(mockCustomer, NotificationType.LEASE_DENIED, "Title",
        "Content");

    // Then
    verify(customerNotificationRepository, times(1)).save(any());
    verify(emitterRepository, times(1)).findAllEmitterStartsWithEmail("test@example.com");
  }

  @Test
  @DisplayName("임대 업자에게 보내는 경우")
  public void testSendToOwner(){
    // Given
    OfficeOwner mockOfficeOwner = mock(OfficeOwner.class);
    when(mockOfficeOwner.getEmail()).thenReturn("test@example.com");

    when(officeOwnerNotificationRepository.save(any())).thenReturn(new OfficeOwnerNotification(
        1L, "Title", "Content", NotificationType.LEASE_REQUEST_ARRIVED, mockOfficeOwner
    ));

    notificationService.sendToOwner(mockOfficeOwner, NotificationType.LEASE_REQUEST_ARRIVED, "Title"
        , "Content");

    verify(officeOwnerNotificationRepository, times(1)).save(any());
    verify(emitterRepository, times(1)).findAllEmitterStartsWithEmail("test@example.com");
  }

  @Test
  @DisplayName("임대 업자의 알림 리스트 조회")
  public void testGetNotificationByOwner() {
    // Given
    String email = "test-owner@example.com";
    Pageable pageable = PageRequest.of(0, 10);
    OfficeOwner mockOfficeOwner = mock(OfficeOwner.class);

    List<OfficeOwnerNotification> mockNotifications = Arrays.asList(
        new OfficeOwnerNotification(1L, "Title1", "Content1", NotificationType.LEASE_REQUEST_ARRIVED, mockOfficeOwner),
        new OfficeOwnerNotification(2L, "Title2", "Content2", NotificationType.LEASE_DENIED, mockOfficeOwner)
    );

    when(officeOwnerRepository.findByEmail(email)).thenReturn(Optional.of(mockOfficeOwner));
    when(officeOwnerNotificationRepository.findAllByOfficeOwner(mockOfficeOwner, pageable))
        .thenReturn(new PageImpl<>(mockNotifications));

    // When
    Page<NotificationResponseDto> result = notificationService.getNotificationByOwner(email, pageable);

    // Then
    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    assertEquals("Title1", result.getContent().get(0).getTitle());
    assertEquals("Title2", result.getContent().get(1).getTitle());
  }

  @Test
  @DisplayName("고객의 알림 리스트 조회")
  public void testGetNotificationByCustomer() {
    // Given
    String email = "test-customer@example.com";
    Pageable pageable = PageRequest.of(0, 10);
    Customer mockCustomer = mock(Customer.class);

    List<CustomerNotification> mockNotifications = Arrays.asList(
        new CustomerNotification(1L, "Title1", "Content1", NotificationType.LEASE_ACCEPTED, mockCustomer),
        new CustomerNotification(2L, "Title2", "Content2", NotificationType.LEASE_DENIED, mockCustomer)
    );

    when(customerRepository.findByEmail(email)).thenReturn(Optional.of(mockCustomer));
    when(customerNotificationRepository.findAllByCustomer(mockCustomer, pageable))
        .thenReturn(new PageImpl<>(mockNotifications));

    // When
    Page<NotificationResponseDto> result = notificationService.getNotificationByCustomer(email, pageable);

    // Then
    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    assertEquals("Title1", result.getContent().get(0).getTitle());
    assertEquals("Title2", result.getContent().get(1).getTitle());
  }
}