package com.dokkebi.officefinder.config.batch;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BatchReaderConfig {

  private final EntityManagerFactory emf;

  private final int PAGE_SIZE = 100;

  @Bean
  @StepScope
  public JpaPagingItemReader<Lease> leaseItemReader(
      @Value("#{jobParameters[expireDate]}") Date expireDate) {

    String query = "SELECT l FROM Lease l WHERE l.leaseStatus = :leaseStatus and "
        + "l.leaseEndDate = :expireDate";

    Map<String, Object> params = new HashMap<>();
    params.put("leaseStatus", LeaseStatus.PROCEEDING);
    params.put("expireDate", expireDate.toLocalDate());

    return new JpaPagingItemReaderBuilder<Lease>()
        .entityManagerFactory(emf)
        .queryString(query)
        .parameterValues(params)
        .pageSize(PAGE_SIZE)
        .name("leaseItemReader")
        .build();
  }
}
