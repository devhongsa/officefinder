package com.dokkebi.officefinder.service.notification.dto;

import com.dokkebi.officefinder.entity.notification.CustomerNotification;
import com.dokkebi.officefinder.entity.notification.OfficeOwnerNotification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {

  private String title;

  private String content;

  private LocalDate createdAt;

  // CustomerNotification을 NotificationResponseDto로 변환
  public static NotificationResponseDto from(CustomerNotification notification) {
    return NotificationResponseDto.builder()
        .title(notification.getTitle())
        .content(notification.getContent())
        .createdAt(notification.getCreatedAt().toLocalDate())
        .build();
  }

  // OfficeOwnerNotification을 NotificationResponseDto로 변환
  public static NotificationResponseDto from(OfficeOwnerNotification notification) {
    return NotificationResponseDto.builder()
        .title(notification.getTitle())
        .content(notification.getContent())
        .createdAt(notification.getCreatedAt().toLocalDate())
        .build();
  }
}
