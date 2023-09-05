package com.dokkebi.officefinder.service.office;

import static com.dokkebi.officefinder.exception.CustomErrorCode.OFFICE_NOT_EXISTS;

import com.dokkebi.officefinder.controller.office.dto.OfficeSearchCond;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.exception.CustomException;
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
        .orElseThrow(() -> new CustomException(OFFICE_NOT_EXISTS));
  }

  public Page<Office> searchOfficeByDetailCondition(OfficeSearchCond cond,
      Pageable pageable) {

    return officeRepository.findBySearchCond(cond, pageable);
  }

  public Page<Office> getAllOffices(String ownerEmail, Pageable pageable) {
    return officeRepository.findByOwnerEmail(ownerEmail, pageable);
  }
}
