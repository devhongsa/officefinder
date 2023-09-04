package com.dokkebi.officefinder.repository.notification;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.notification.CustomerNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerNotificationRepository extends JpaRepository<CustomerNotification, Long> {

  Page<CustomerNotification> findAllByCustomer(Customer customer, Pageable pageable);
}
