package com.dokkebi.officefinder.service.schedule;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class ScheduledService {

  private final JobLauncher jobLauncher;

  private final Job updatedExpiredLeaseJob;

  @Scheduled(cron = "0 0 0 * * ?")
  public void runUpdateExpiredJob() throws Exception{
    LocalDate expireDate = LocalDate.now().minusDays(1);

    JobParameters jobParameters = new JobParametersBuilder()
        .addDate("expireDate", java.sql.Date.valueOf(expireDate))
        .toJobParameters();

    jobLauncher.run(updatedExpiredLeaseJob, jobParameters);
  }
}
