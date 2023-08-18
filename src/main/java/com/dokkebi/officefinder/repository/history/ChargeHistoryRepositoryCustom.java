package com.dokkebi.officefinder.repository.history;

import com.dokkebi.officefinder.entity.PointChargeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChargeHistoryRepositoryCustom {

  Page<PointChargeHistory> findByCustomerEmail(String customerEmail, Pageable pageable);

}
