package com.dokkebi.officefinder.config.batch;

import com.dokkebi.officefinder.entity.lease.Lease;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BatchWriterConfig {

  private final EntityManagerFactory emf;

  @Bean
  public JpaItemWriter<Lease> leaseItemWriter(){
    JpaItemWriter<Lease> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(emf);
    return jpaItemWriter;
  }
}
