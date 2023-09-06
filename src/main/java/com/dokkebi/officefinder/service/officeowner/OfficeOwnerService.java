package com.dokkebi.officefinder.service.officeowner;

import com.dokkebi.officefinder.controller.officeowner.dto.OfficeOwnerInfoDto;
import com.dokkebi.officefinder.controller.officeowner.dto.OfficeOwnerOverViewDto;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.security.TokenProvider;
import com.dokkebi.officefinder.service.officeowner.dto.OfficeOwnerServiceDto.RentalStatusDto;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OfficeOwnerService {

  private final OfficeOwnerRepository officeOwnerRepository;
  private final LeaseRepository leaseRepository;
  private final OfficeRepository officeRepository;
  private final TokenProvider tokenProvider;

  private final int PERIOD = 5;

  private final LocalDate today = LocalDate.now();
  private final LocalDate start = today.minusMonths(PERIOD);
  private final LocalDate startDate = LocalDate.of(start.getYear(), start.getMonth(), 1);
  private final List<LeaseStatus> leaseStatus = Arrays.asList(LeaseStatus.EXPIRED,
      LeaseStatus.PROCEEDING);

  public OfficeOwnerInfoDto getAgentInfo(Long officeOwnerId) {
    OfficeOwner officeOwner = officeOwnerRepository.findById(officeOwnerId)
        .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

    return OfficeOwnerInfoDto.from(officeOwner);
  }

  public OfficeOwnerOverViewDto getAgentOverViewInfo(Long officeOwnerId) {
    OfficeOwner officeOwner = officeOwnerRepository.findById(officeOwnerId)
        .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

    return OfficeOwnerOverViewDto.from(officeOwner);
  }

  @Transactional
  public void changeAgentProfileImage(String userImagePath, String officeOwnerEmail) {
    OfficeOwner officeOwner = officeOwnerRepository.findByEmail(officeOwnerEmail)
        .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

    officeOwner.changeOfficeOwnerProfileImage(userImagePath);
  }

  @Transactional
  public void changeAgentName(String newAgentName, Long officeOwnerId){
    OfficeOwner officeOwner = officeOwnerRepository.findById(officeOwnerId)
        .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

    officeOwner.changeOwnerName(newAgentName);
  }

  public HashMap<String, Long> getOfficeRevenue(Long officeId, String jwt) {
    Long officeOwnerId = tokenProvider.getUserId(jwt);

    Office office = officeRepository.findByIdAndOwnerId(officeId, officeOwnerId)
        .orElseThrow(() -> new CustomException(CustomErrorCode.OFFICE_NOT_OWNED_BY_OWNER));

    List<Lease> leases = leaseRepository.findByOfficeIdAndLeaseStartDateBetweenAndLeaseStatusInOrderByLeaseStartDate(
        office.getId(), startDate, today, leaseStatus);

    return getRevenue(leases);
  }

  public HashMap<String, Long> getOfficesTotalRevenue(String jwt) {
    Long officeOwnerId = tokenProvider.getUserId(jwt);

    List<Office> offices = officeRepository.findByOwnerId(officeOwnerId);

    List<Lease> leases = leaseRepository.findByOfficeInAndLeaseStartDateBetweenAndLeaseStatusInOrderByLeaseStartDate(
        offices, startDate, today, leaseStatus);

    return getRevenue(leases);
  }

  public RentalStatusDto getOfficeRentalStatus(Long officeId, String jwt) {
    Long officeOwnerId = tokenProvider.getUserId(jwt);
    Office office = officeRepository.findByIdAndOwnerId(officeId, officeOwnerId)
        .orElseThrow(() -> new CustomException(CustomErrorCode.OFFICE_NOT_OWNED_BY_OWNER));

    int countProceeding = leaseRepository.countByOfficeIdAndLeaseStatus(office.getId(),
        LeaseStatus.PROCEEDING);

    double leaseRate =
        Math.round((double) countProceeding / office.getMaxRoomCount() * 100.0) / 100.0;

    return new RentalStatusDto(office.getMaxRoomCount(), countProceeding, leaseRate);
  }

  public RentalStatusDto getOfficeOverallRentalStatus(String jwt) {
    Long officeOwnerId = tokenProvider.getUserId(jwt);

    List<Office> offices = officeRepository.findByOwnerId(officeOwnerId);

    int totalRoomCount = offices.stream().mapToInt(Office::getMaxRoomCount).sum();

    int countProceeding = leaseRepository.countByOfficeInAndLeaseStatus(offices,
        LeaseStatus.PROCEEDING);

    double leaseRate =
        Math.round((double) countProceeding / totalRoomCount * 100.0) / 100.0;

    return new RentalStatusDto(totalRoomCount, countProceeding, leaseRate);
  }

  private HashMap<String, Long> getRevenue(List<Lease> leases) {
    HashMap<String, Long> revenueMap = new HashMap<>();

    for (int i = 0; i <= PERIOD; i++) {
      String key = startDate.plusMonths(i).toString().substring(0, 7);
      revenueMap.put(key, 0L);
    }

    // 시작일 기준 매출
    for (Lease lease : leases) {
      String key = lease.getLeaseStartDate().toString().substring(0, 7);
      Long value = lease.getPrice();

      revenueMap.put(key, revenueMap.get(key) + value);
    }
    return revenueMap;
  }
}