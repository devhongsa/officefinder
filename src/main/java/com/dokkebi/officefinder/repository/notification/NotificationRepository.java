package com.dokkebi.officefinder.repository.notification;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.notification.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  List<Notification> findAllByCustomer(Customer customer);
}
