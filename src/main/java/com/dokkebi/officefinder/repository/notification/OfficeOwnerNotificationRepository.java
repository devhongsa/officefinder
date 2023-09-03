package com.dokkebi.officefinder.repository.notification;

import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.notification.OfficeOwnerNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeOwnerNotificationRepository extends JpaRepository<OfficeOwnerNotification, Long> {
  Page<OfficeOwnerNotification> findAllByOfficeOwner(OfficeOwner officeOwner, Pageable pageable);
}
