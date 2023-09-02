package com.dokkebi.officefinder.service.schedule;

import java.sql.Date;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class ScheduledService {

  private final JobLauncher jobLauncher;

  private final Job updateLeaseJob;

  private final Job alarmLeaseStartJob;

  private final Job alarmLeaseExpireJob;

  @Scheduled(cron = "0 0 0 * * ?")
  public void executeUpdateLeases() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        .addDate("today", Date.valueOf(LocalDate.now()))
        .toJobParameters();

    jobLauncher.run(updateLeaseJob, jobParameters);
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void sendLeaseStartNotifications() throws Exception{
    // 몇일 전 까지 알림을 보낼지 지정 가능(ex: 3,2,1일전 및 당일에 알림을 보내고자 한다면 "3,2,1,0"
    String daysToNotify = "3,2,1,0";
    JobParameters jobParameters = new JobParametersBuilder()
        .addDate("startDate", Date.valueOf(LocalDate.now()))
        .addString("daysToNotify", daysToNotify)
        .toJobParameters();

    jobLauncher.run(alarmLeaseStartJob, jobParameters);
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void sendLeaseExpirationNotifications() throws Exception{
    String daysToNotify = "3,2,1,0";
    JobParameters jobParameters = new JobParametersBuilder()
        .addDate("expireDate", Date.valueOf(LocalDate.now().minusDays(1)))
        .addString("daysToNotify", daysToNotify)
        .toJobParameters();

    jobLauncher.run(alarmLeaseExpireJob, jobParameters);
  }
}
