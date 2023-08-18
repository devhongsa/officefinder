package com.dokkebi.officefinder.repository.lease;

import com.dokkebi.officefinder.entity.lease.Lease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaseRepositoryCustom {

  Page<Lease> findByCustomerId(Long customerId, Pageable pageable);
}
