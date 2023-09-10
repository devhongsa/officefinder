package com.dokkebi.officefinder.service.office;

import static com.dokkebi.officefinder.exception.CustomErrorCode.EMAIL_NOT_REGISTERED;
import static com.dokkebi.officefinder.exception.CustomErrorCode.OFFICE_NOT_EXISTS;
import static com.dokkebi.officefinder.exception.CustomErrorCode.OFFICE_NOT_OWNED_BY_OWNER;

import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeModifyRequestDto;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficeCondition;
import com.dokkebi.officefinder.entity.office.OfficeLocation;
import com.dokkebi.officefinder.entity.office.OfficePicture;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.repository.office.condition.OfficeConditionRepository;
import com.dokkebi.officefinder.repository.office.location.OfficeLocationRepository;
import com.dokkebi.officefinder.repository.office.picture.OfficePictureRepository;
import com.dokkebi.officefinder.service.office.dto.OfficeConditionDto;
import com.dokkebi.officefinder.service.office.dto.OfficeLocationDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OfficeService {

  private final OfficeRepository officeRepository;
  private final OfficeLocationRepository officeLocationRepository;
  private final OfficeConditionRepository officeConditionRepository;
  private final OfficePictureRepository officePictureRepository;
  private final OfficeOwnerRepository ownerRepository;

  public Long createOfficeInfo(OfficeCreateRequestDto request, List<String> imageList,
      String ownerEmail) {
    OfficeOwner officeOwner = ownerRepository.findByEmail(ownerEmail)
        .orElseThrow(() -> new CustomException(EMAIL_NOT_REGISTERED));

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

    for (String imageUrl : imageList) {
      officePictureRepository.save(OfficePicture.createFromPath(imageUrl, savedOffice));
    }

    return savedOffice.getId();
  }

  public Long modifyOfficeInfo(OfficeModifyRequestDto request, List<String> imageList,
      String ownerEmail, Long officeId) {

    Office office = officeRepository.findByOfficeId(officeId)
        .orElseThrow(() -> new CustomException(OFFICE_NOT_EXISTS));

    // 다른 임대업자가 수정하는 것을 금지
    validateCorrectOwner(ownerEmail, office);

    modifyOfficeCondition(office.getOfficeCondition(), OfficeConditionDto.fromRequest(request));
    modifyOfficeLocation(office.getOfficeLocation(), OfficeLocationDto.fromRequest(request));
    office.modifyFromRequest(request);

    List<OfficePicture> oldOfficePicture = officePictureRepository.findByOfficeId(officeId);
    officePictureRepository.deleteAll(oldOfficePicture);

    for (String imageUrl : imageList) {
      officePictureRepository.save(OfficePicture.createFromPath(imageUrl, office));
    }

    return office.getId();
  }

  public void deleteOfficeInfo(Long officeId) {
    Office office = officeRepository.findByOfficeId(officeId)
        .orElseThrow(() -> new CustomException(OFFICE_NOT_EXISTS));

    officeRepository.delete(office);
  }

  private void validateCorrectOwner(String ownerEmail, Office office) {
    if (!office.getOwner().getEmail().equals(ownerEmail)) {
      throw new CustomException(OFFICE_NOT_OWNED_BY_OWNER);
    }
  }

  private void modifyOfficeCondition(OfficeCondition condition, OfficeConditionDto request) {
    condition.modifyFromRequest(request);
  }

  private void modifyOfficeLocation(OfficeLocation location, OfficeLocationDto request) {
    location.modifyFromRequest(request);
  }
}
