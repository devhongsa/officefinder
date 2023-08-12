package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.PointChargeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeHistoryRepository extends JpaRepository<PointChargeHistory, Long> {

}
