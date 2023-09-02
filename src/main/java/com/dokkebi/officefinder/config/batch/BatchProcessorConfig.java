package com.dokkebi.officefinder.config.batch;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.entity.type.NotificationType;
import com.dokkebi.officefinder.service.notification.NotificationService;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BatchProcessorConfig {

  private final NotificationService notificationService;

  @Bean
  @StepScope
  public ItemProcessor<Lease, Lease> leaseEndItemProcessor() {
    return lease -> {
      lease.changeLeaseStatus(LeaseStatus.EXPIRED);
      return lease;
    };
  }

  @Bean
  @StepScope
  public ItemProcessor<Lease, Lease> leaseStartItemProcessor() {
    return lease -> {
      lease.changeLeaseStatus(LeaseStatus.PROCEEDING);
      return lease;
    };
  }

  @Bean
  @StepScope
  public ItemProcessor<Lease, Lease> alarmLeaseStartProcessor(
      @Value("#{jobParameters[startDate]}") Date startDate
  ) {
    LocalDate currentDate = startDate.toLocalDate();

    return lease -> {
      LocalDate leaseStartDate = lease.getLeaseStartDate();
      int daysDifference = (int) ChronoUnit.DAYS.between(leaseStartDate, currentDate);
      String officeName = lease.getOffice().getName();

      if (daysDifference == 0) {
        notificationService.sendToCustomer(lease.getCustomer(), NotificationType.LEASE_PROCEED,
            "임대 시작", officeName + "에 대한 임대가 시작되었습니다 :)");
      } else {
        notificationService.sendToCustomer(lease.getCustomer(), NotificationType.LEASE_REMINDER,
            "임대 시작 임박", officeName + "에 대한 임대 시작 " + daysDifference + "일 남았습니다 :)");
      }
      return lease;
    };
  }

  @Bean
  @StepScope
  public ItemProcessor<Lease, Lease> alarmLeaseEndProcessor(
      @Value("#{jobParameters[expireDate]}") Date expireDate) {

    LocalDate currentDate = expireDate.toLocalDate();

    return lease -> {
      LocalDate leaseEndDate = lease.getLeaseEndDate();
      int daysDifference = (int) ChronoUnit.DAYS.between(leaseEndDate, currentDate);
      String officeName = lease.getOffice().getName();

      if (daysDifference == 0) {
        notificationService.sendToCustomer(lease.getCustomer(), NotificationType.LEASE_EXPIRED,
            "임대 만료", officeName + "에 대한 임대가 만료되었습니다. 이용해 주셔서 감사합니다 :)");
      } else {
        notificationService.sendToCustomer(lease.getCustomer(), NotificationType.LEASE_REMINDER,
            "임대 만료 임박", officeName + "에 대한 임대가 만료 " + daysDifference + "일 남았습니다 :)");
      }

      return lease;
    };
  }
}

