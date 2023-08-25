package com.dokkebi.officefinder.repository.history;

import com.dokkebi.officefinder.entity.PointChargeHistory;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeHistoryRepository extends JpaRepository<PointChargeHistory, Long>,
    ChargeHistoryRepositoryCustom {
  Set<PointChargeHistory> findTop10ByCustomerIdOrderByCreatedAtDesc(Long customerId);
  Page<PointChargeHistory> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
}