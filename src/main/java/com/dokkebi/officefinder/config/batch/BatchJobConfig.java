package com.dokkebi.officefinder.config.batch;

import com.dokkebi.officefinder.entity.lease.Lease;
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
import org.springframework.beans.factory.annotation.Qualifier;
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
  public Flow updateLeaseEndFlow(
      @Qualifier("leaseEndItemReader") JpaPagingItemReader<Lease> reader,
      @Qualifier("leaseEndItemProcessor") ItemProcessor<Lease, Lease> processor,
      JpaItemWriter<Lease> writer){

    return new FlowBuilder<Flow>("updateLeaseEndFlow")
        .start(stepBuilderFactory.get("updateLeaseEndStep")
        .<Lease, Lease>chunk(CHUNK_SIZE)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build())
        .build();
  }

  @Bean
  public Flow updateLeaseStartFlow(
      @Qualifier("leaseStartItemReader") JpaPagingItemReader<Lease> reader,
      @Qualifier("leaseStartItemProcessor") ItemProcessor<Lease, Lease> processor,
      JpaItemWriter<Lease> writer){

    return new FlowBuilder<Flow>("updateLeaseStartFlow")
        .start(stepBuilderFactory.get("updateLeaseStartStep")
            .<Lease, Lease>chunk(CHUNK_SIZE)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build())
        .build();
  }

  @Bean
  public Job updateLeaseJob(@Qualifier("updateLeaseEndFlow") Flow endFlow,
      @Qualifier("updateLeaseStartFlow") Flow startFlow){

    return jobBuilderFactory.get("updateLeaseJob")
        .start(splitFlow(endFlow, startFlow))
        .end()
        .build();
  }
  public Flow splitFlow(Flow endFlow, Flow startFlow) {
    return new FlowBuilder<SimpleFlow>("splitFlow")
        .split(batchTaskExecutor())
        .add(endFlow, startFlow)
        .build();
  }
}
