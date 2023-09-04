package com.dokkebi.officefinder.entity.notification;

import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.type.NotificationType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
public class OfficeOwnerNotification extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String content;

  @Enumerated(EnumType.STRING)
  private NotificationType notificationType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "office_owner_id")
  private OfficeOwner officeOwner;

  public static OfficeOwnerNotification createOwnerNotification(OfficeOwner officeOwner,
      NotificationType type, String title, String content) {
    return OfficeOwnerNotification.builder()
        .officeOwner(officeOwner)
        .notificationType(type)
        .title(title)
        .content(content)
        .build();
  }
}
