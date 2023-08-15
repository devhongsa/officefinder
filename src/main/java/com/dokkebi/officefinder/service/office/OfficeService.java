package com.dokkebi.officefinder.service.office;

import com.dokkebi.officefinder.controller.office.dto.OfficeBasicSearchCond;
import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeDetailSearchCond;
import com.dokkebi.officefinder.controller.office.dto.OfficeModifyRequestDto;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficeCondition;
import com.dokkebi.officefinder.entity.office.OfficeLocation;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.repository.office.condition.OfficeConditionRepository;
import com.dokkebi.officefinder.repository.office.location.OfficeLocationRepository;
import com.dokkebi.officefinder.service.office.dto.OfficeConditionDto;
import com.dokkebi.officefinder.service.office.dto.OfficeLocationDto;
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
  private final OfficeOwnerRepository ownerRepository;
  private final OfficeRedisService officeRedisService;

  @Transactional
  public Long createOfficeInfo(OfficeCreateRequestDto request, String ownerEmail) {
    OfficeOwner officeOwner = ownerRepository.findByEmail(ownerEmail)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

    Office office = Office.createFromRequest(request, officeOwner);

    OfficeCondition officeCondition = OfficeCondition.createFromRequest(
        office, OfficeConditionDto.fromRequest(request)
    );

    OfficeLocation officeLocation = OfficeLocation.createFromRequest(
        office, OfficeLocationDto.fromRequest(request)
    );

    Office savedOffice = officeRepository.save(office);
    officeLocationRepository.save(officeLocation);
    officeConditionRepository.save(officeCondition);

    officeRedisService.setRemainRoom(request.getOfficeName(), request.getRemainRoom());

    return savedOffice.getId();
  }

  @Transactional
  public Long modifyOfficeInfo(OfficeModifyRequestDto request, String ownerEmail, Long officeId) {
    Office office = officeRepository.findByOfficeId(officeId)
        .orElseThrow(() -> new IllegalArgumentException("해당 오피스는 존재하지 않습니다."));

    // 다른 임대업자가 수정하는 것을 금지
    validateCorrectOwner(ownerEmail, office);

    modifyOfficeCondition(office.getOfficeCondition(), OfficeConditionDto.fromRequest(request));
    modifyOfficeLocation(office.getOfficeLocation(), OfficeLocationDto.fromRequest(request));

    office.modifyFromRequest(request);

    officeRedisService.setRemainRoom(request.getOfficeName(), request.getRemainRoom());

    return office.getId();
  }

  private void validateCorrectOwner(String ownerEmail, Office office) {
    if (!office.getOwner().getEmail().equals(ownerEmail)) {
      throw new IllegalArgumentException("잘못된 접근입니다.");
    }
  }

  @Transactional
  public void modifyOfficeCondition(OfficeCondition condition, OfficeConditionDto request) {
    condition.modifyFromRequest(request);
  }

  @Transactional
  public void modifyOfficeLocation(OfficeLocation location, OfficeLocationDto request) {
    location.modifyFromRequest(request);
  }

  @Transactional
  public void deleteOfficeInfo(Long officeId) {
    Office office = officeRepository.findByOfficeId(officeId)
        .orElseThrow(() -> new IllegalArgumentException("해당 오피스는 존재하지 않습니다."));

    officeRepository.delete(office);
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
