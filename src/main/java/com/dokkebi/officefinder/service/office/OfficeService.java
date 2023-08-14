package com.dokkebi.officefinder.service.office;

import com.dokkebi.officefinder.controller.office.dto.OfficeBasicSearchCond;
import com.dokkebi.officefinder.controller.office.dto.OfficeDetailSearchCond;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.repository.office.condition.OfficeConditionRepository;
import com.dokkebi.officefinder.repository.office.location.OfficeLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfficeService {

  private final OfficeRepository officeRepository;
  private final OfficeLocationRepository officeLocationRepository;
  private final OfficeConditionRepository officeConditionRepository;

  @Transactional
  public Office createOfficeInfo() {
    return null;
  }

  @Transactional
  public Office modifyOfficeInfo() {
    return null;
  }

  @Transactional
  public Office deleteOfficeInfo() {
    return null;
  }

  public Page<Office> searchOfficeByBasicCondition(OfficeBasicSearchCond cond,
      Pageable pageable) {

    return officeRepository.findByBasicCondition(cond, pageable);
  }

  public Page<Office> searchOfficeByDetailCondition(OfficeDetailSearchCond cond,
      Pageable pageable) {

    return officeRepository.findByDetailCondition(cond, pageable);
  }
}
