package com.dokkebi.officefinder.service.notification.dto;

import com.dokkebi.officefinder.entity.notification.Notification;
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

  private LocalDateTime createdAt;

  public static NotificationResponseDto NotificationToResponseDto(Notification notification){
    return NotificationResponseDto.builder()
        .title(notification.getTitle())
        .content(notification.getContent())
        .createdAt(notification.getCreatedAt())
        .build();
  }

  public static NotificationResponseDto makeNotificationResponseDto(String title, String content){
    return NotificationResponseDto.builder()
        .title(title)
        .content(content)
        .createdAt(LocalDateTime.now())
        .build();
  }
}
