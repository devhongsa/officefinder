package com.dokkebi.officefinder.repository.notification;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.notification.CustomerNotification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerNotificationRepository extends JpaRepository<CustomerNotification, Long> {

  List<CustomerNotification> findAllByCustomer(Customer customer);
}
