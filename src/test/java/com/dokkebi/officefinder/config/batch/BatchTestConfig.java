package com.dokkebi.officefinder.config.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@EnableBatchProcessing
public class BatchTestConfig {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job updateLeaseJob;

  @Bean
  public JobLauncherTestUtils jobLauncherTestUtils() {
    JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
    jobLauncherTestUtils.setJobLauncher(jobLauncher);
    jobLauncherTestUtils.setJob(updateLeaseJob);
    return jobLauncherTestUtils;
  }
}
