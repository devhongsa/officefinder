package com.dokkebi.officefinder.repository.office;

import com.dokkebi.officefinder.controller.office.dto.OfficeSearchCond;
import com.dokkebi.officefinder.entity.office.Office;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OfficeRepositoryCustom {

  Page<Office> findBySearchCond(OfficeSearchCond cond, Pageable pageable);

  Page<Office> findByOwnerEmail(String ownerEmail, Pageable pageable);

  Optional<Office> findByOfficeId(Long id);
}
