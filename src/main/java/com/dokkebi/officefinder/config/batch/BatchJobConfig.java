package com.dokkebi.officefinder.config.batch;

import com.dokkebi.officefinder.entity.lease.Lease;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class BatchJobConfig {

  private final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  private final JpaPagingItemReader<Lease> leaseItemReader;
  private final ItemProcessor<Lease, Lease> leaseItemProcessor;
  private final JpaItemWriter<Lease> leaseItemWriter;

  private final int CHUNK_SIZE = 100;

  @Bean
  public Step updateLeaseStep(){
    return stepBuilderFactory.get("updateLeaseStep")
        .<Lease, Lease>chunk(CHUNK_SIZE)
        .reader(leaseItemReader)
        .processor(leaseItemProcessor)
        .writer(leaseItemWriter)
        .build();
  }

  @Bean
  public Job updatedExpiredLeaseJob(Step updateLeaseStep){
    return jobBuilderFactory.get("updatedExpiredLeaseJob")
        .start(updateLeaseStep)
        .build();
  }
}
