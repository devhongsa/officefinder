package com.dokkebi.officefinder.repository.lease;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaseRepositoryCustom {

  Page<Lease> findByCustomerId(Long customerId, Pageable pageable);

  Page<Lease> findByOfficeIdAndLeaseStatus(Long officeId, LeaseStatus leaseStatus,
      Pageable pageable);

  List<Lease> findOfficeRevenueLastSixMonth(long officeId, LocalDate startDate, LocalDate today,
      List<LeaseStatus> leaseStatusList);

  List<Lease> findTotalRevenueLastSixMonth(List<Long> offices, LocalDate startDate,
      LocalDate today, List<LeaseStatus> leaseStatusList);

  Long countOfficeRoomInUse(Long officeId, List<LeaseStatus> leaseStatus, LocalDate startDate,
      LocalDate endDate);

  Optional<Lease> findByLeaseId(long leaseId);
}