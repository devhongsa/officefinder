package com.dokkebi.officefinder.config.batch;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
  public JpaPagingItemReader<Lease> leaseEndItemReader(
      @Value("#{jobParameters[today]}") Date today
  ) {
    LocalDate yesterday = today.toLocalDate().minusDays(1);

    String query = "SELECT l FROM Lease l WHERE l.leaseStatus = :leaseStatus and "
        + "l.leaseEndDate = :expireDate";

    Map<String, Object> params = new HashMap<>();
    params.put("leaseStatus", LeaseStatus.PROCEEDING);
    params.put("expireDate", yesterday);

    return new JpaPagingItemReaderBuilder<Lease>()
        .entityManagerFactory(emf)
        .queryString(query)
        .parameterValues(params)
        .pageSize(PAGE_SIZE)
        .name("leaseEndItemReader")
        .build();
  }

  @Bean
  @StepScope
  public JpaPagingItemReader<Lease> leaseStartItemReader(
      @Value("#{jobParameters[today]}") Date today
  ) {
    String query = "SELECT l from Lease l WHERE l.leaseStatus = :leaseStatus and "
        + "l.leaseStartDate = :startDate";

    Map<String, Object> params = new HashMap<>();
    params.put("leaseStatus", LeaseStatus.AWAIT);
    params.put("startDate", today.toLocalDate());

    return new JpaPagingItemReaderBuilder<Lease>()
        .entityManagerFactory(emf)
        .queryString(query)
        .parameterValues(params)
        .pageSize(PAGE_SIZE)
        .name("leaseStartItemReader")
        .build();
  }

  @Bean
  @StepScope
  public JpaPagingItemReader<Lease> alarmStartItemReader(
      @Value("#{jobParameters[startDate]}") Date startDate,
      @Value("#{jobParameters[daysToNotify]}") String daysToNotify) {

    String query = "SELECT l from Lease l WHERE l.leaseStartDate IN :startDates";
    Map<String, Object> params = new HashMap<>();
    params.put("startDates", generateDatesFromDayToNotify(daysToNotify, startDate));

    return new JpaPagingItemReaderBuilder<Lease>()
        .entityManagerFactory(emf)
        .queryString(query)
        .parameterValues(params)
        .pageSize(PAGE_SIZE)
        .name("alarmStartItemReader")
        .build();
  }

  @Bean
  @StepScope
  public JpaPagingItemReader<Lease> alarmExpireItemReader(
      @Value("#{jobParameters[expireDate]}") Date expireDate,
      @Value("#{jobParameters[daysToNotify]}") String daysToNotify) {
    String query = "SELECT l from Lease l WHERE l.leaseEndDate IN :endDates";
    Map<String, Object> params = new HashMap<>();
    params.put("endDates", generateDatesFromDayToNotify(daysToNotify, expireDate));

    return new JpaPagingItemReaderBuilder<Lease>()
        .entityManagerFactory(emf)
        .queryString(query)
        .parameterValues(params)
        .pageSize(PAGE_SIZE)
        .name("alarmExpireItemReader")
        .build();
  }

  private List<LocalDate> generateDatesFromDayToNotify(String daysToNotify, Date today) {
    return Arrays.stream(daysToNotify.split(","))
        .map(Integer::parseInt)
        .map(days -> today.toLocalDate().minusDays(days))
        .collect(Collectors.toList());
  }
}
