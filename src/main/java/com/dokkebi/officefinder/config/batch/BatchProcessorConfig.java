package com.dokkebi.officefinder.config.batch;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchProcessorConfig {

  @Bean
  public ItemProcessor<Lease, Lease> leaseStartItemProcessor(){
    return new ItemProcessor<Lease, Lease>() {
      @Override
      public Lease process(Lease lease) throws Exception {
        lease.changeLeaseStatus(LeaseStatus.PROCEEDING);
        return lease;
      }
    };
  }

  @Bean
  public ItemProcessor<Lease, Lease> leaseEndItemProcessor(){
    return new ItemProcessor<Lease, Lease>() {
      @Override
      public Lease process(Lease lease) throws Exception {
        lease.changeLeaseStatus(LeaseStatus.EXPIRED);
        return lease;
      }
    };
  }
}
