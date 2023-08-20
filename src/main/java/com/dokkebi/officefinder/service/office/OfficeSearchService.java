package com.dokkebi.officefinder.service.office;

import com.dokkebi.officefinder.controller.office.dto.OfficeBasicSearchCond;
import com.dokkebi.officefinder.controller.office.dto.OfficeDetailSearchCond;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfficeSearchService {

  private final OfficeRepository officeRepository;

  public Office getOfficeInfo(Long officeId) {
    return officeRepository.findByOfficeId(officeId)
        .orElseThrow(() -> new IllegalArgumentException("해당 오피스는 존재하지 않습니다."));
  }

  public Page<Office> searchOfficeByBasicCondition(OfficeBasicSearchCond cond,
      Pageable pageable) {

    return officeRepository.findByBasicCondition(cond, pageable);
  }

  public Page<Office> searchOfficeByDetailCondition(OfficeDetailSearchCond cond,
      Pageable pageable) {

    return officeRepository.findByDetailCondition(cond, pageable);
  }

  public Page<Office> getAllOffices(String ownerEmail, Pageable pageable) {
    return officeRepository.findByOwnerEmail(ownerEmail, pageable);
  }
}
