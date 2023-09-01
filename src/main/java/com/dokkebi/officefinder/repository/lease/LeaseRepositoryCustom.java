package com.dokkebi.officefinder.repository.lease;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaseRepositoryCustom {

  Page<Lease> findByCustomerId(Long customerId, Pageable pageable);

  Page<Lease> findByOfficeIdAndLeaseStatus(Long officeId, LeaseStatus leaseStatus,Pageable pageable);
}