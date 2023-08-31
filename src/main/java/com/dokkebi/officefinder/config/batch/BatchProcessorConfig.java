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
  public ItemProcessor<Lease, Lease> leaseStartItemProcessor(
      @Value("#{jobParameters[startDate]}") Date startDate
  ){
    return lease -> handleLeaseStart(lease, startDate);
  }

  @Bean
  @StepScope
  public ItemProcessor<Lease, Lease> leaseEndItemProcessor(
      @Value("#{jobParameters[expireDate]}") Date expireDate
  ){
    return lease -> handleLeaseEnd(lease, expireDate);
  }

  private Lease handleLeaseStart(Lease lease, Date referenceDate) {
    LocalDate currentDate = referenceDate.toLocalDate();
    LocalDate leaseStartDate = lease.getLeaseStartDate();
    int daysDifference = (int) ChronoUnit.DAYS.between(currentDate, leaseStartDate);

    if(daysDifference > 0){
      notificationService.sendToCustomer(lease.getCustomer(), NotificationType.LEASE_REMINDER,
          "임대 시작 임박", "임대 시작 " + daysDifference + "일 남았습니다 :)");
    }else{
      lease.changeLeaseStatus(LeaseStatus.PROCEEDING);
      notificationService.sendToCustomer(lease.getCustomer(), NotificationType.LEASE_PROCEED,
          "임대 시작", "임대 시작 당일입니다 :)");
    }

    return lease;
  }

  private Lease handleLeaseEnd(Lease lease, Date referenceDate) {
    LocalDate currentDate = referenceDate.toLocalDate();
    LocalDate leaseEndDate = lease.getLeaseEndDate();
    int daysDifference = (int) ChronoUnit.DAYS.between(currentDate, leaseEndDate);

    if(daysDifference > 0){
      notificationService.sendToCustomer(lease.getCustomer(), NotificationType.LEASE_REMINDER,
          "임대 만료 임박", "임대 만료 " + daysDifference + "일 남았습니다 :)");
    }else{
      lease.changeLeaseStatus(LeaseStatus.EXPIRED);
      notificationService.sendToCustomer(lease.getCustomer(), NotificationType.LEASE_EXPIRED,
          "임대 만료", "임대가 만료되었습니다. 이용해 주셔서 감사합니다 :)");
    }

    return lease;
  }
}

