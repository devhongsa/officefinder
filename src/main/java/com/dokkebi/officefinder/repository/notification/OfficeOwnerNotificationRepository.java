package com.dokkebi.officefinder.repository.notification;

import com.dokkebi.officefinder.entity.notification.OfficeOwnerNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeOwnerNotificationRepository extends
    JpaRepository<OfficeOwnerNotification, Long> {

}
