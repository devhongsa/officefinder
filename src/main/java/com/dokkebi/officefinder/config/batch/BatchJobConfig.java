package com.dokkebi.officefinder.config.batch;

import com.dokkebi.officefinder.entity.lease.Lease;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.flow.Flow;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class BatchJobConfig {

  private final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  private final int CHUNK_SIZE = 100;

  @Bean
  public TaskExecutor batchTaskExecutor(){
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setMaxPoolSize(10);
    taskExecutor.setCorePoolSize(5);
    taskExecutor.setQueueCapacity(100);
    taskExecutor.initialize();
    return taskExecutor;
  }
  @Bean
  public Flow updateLeaseEndFlow(JpaPagingItemReader<Lease> leaseEndItemReader,
      ItemProcessor<Lease, Lease> leaseEndItemProcessor,
      JpaItemWriter<Lease> leaseItemWriter){

    return new FlowBuilder<Flow>("updateLeaseEndFlow")
        .start(stepBuilderFactory.get("updateLeaseEndStep")
            .<Lease, Lease>chunk(CHUNK_SIZE)
            .reader(leaseEndItemReader)
            .processor(leaseEndItemProcessor)
            .writer(leaseItemWriter)
            .build())
        .build();
  }

  @Bean
  public Flow updateLeaseStartFlow(JpaPagingItemReader<Lease> leaseStartItemReader,
      ItemProcessor<Lease, Lease> leaseStartItemProcessor,
      JpaItemWriter<Lease> leaseItemWriter){

    return new FlowBuilder<Flow>("updateLeaseStartFlow")
        .start(stepBuilderFactory.get("updateLeaseStartStep")
            .<Lease, Lease>chunk(CHUNK_SIZE)
            .reader(leaseStartItemReader)
            .processor(leaseStartItemProcessor)
            .writer(leaseItemWriter)
            .build())
        .build();
  }

  @Bean
  public Step alarmLeaseStartStep(JpaPagingItemReader<Lease> alarmStartItemReader,
      ItemProcessor<Lease, Lease> alarmLeaseStartProcessor,
      JpaItemWriter<Lease> leaseItemWriter) {

    return stepBuilderFactory.get("alarmLeaseStartStep")
        .<Lease, Lease>chunk(CHUNK_SIZE)
        .reader(alarmStartItemReader)
        .processor(alarmLeaseStartProcessor)
        .writer(leaseItemWriter)
        .build();
  }

  @Bean
  public Step alarmLeaseExpireStep(JpaPagingItemReader<Lease> alarmExpireItemReader,
      ItemProcessor<Lease, Lease> alarmLeaseEndProcessor,
      JpaItemWriter<Lease> leaseItemWriter) {

    return stepBuilderFactory.get("alarmLeaseExpireStep")
        .<Lease, Lease>chunk(CHUNK_SIZE)
        .reader(alarmExpireItemReader)
        .processor(alarmLeaseEndProcessor)
        .writer(leaseItemWriter)
        .build();
  }

  @Bean
  public Job updateLeaseJob(Flow updateLeaseEndFlow, Flow updateLeaseStartFlow) {
    return jobBuilderFactory.get("updateLeaseJob")
        .start(splitFlow(updateLeaseEndFlow, updateLeaseStartFlow))
        .end()
        .build();
  }

  @Bean
  public Job alarmLeaseStartJob(Step alarmLeaseStartStep) {
    return jobBuilderFactory.get("alarmLeaseStartJob")
        .start(alarmLeaseStartStep)
        .build();
  }

  @Bean
  public Job alarmLeaseExpireJob(Step alarmLeaseExpireStep) {
    return jobBuilderFactory.get("alarmLeaseExpireJob")
        .start(alarmLeaseExpireStep)
        .build();
  }

  private Flow splitFlow(Flow endFlow, Flow startFlow) {
    return new FlowBuilder<SimpleFlow>("splitFlow")
        .split(batchTaskExecutor())
        .add(endFlow, startFlow)
        .build();
  }
}
