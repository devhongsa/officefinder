package com.dokkebi.officefinder.repository.lease;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeaseRepository extends JpaRepository<Lease, Long>, LeaseRepositoryCustom {

  @Query("select l from Lease l join fetch l.customer join fetch l.office where l.id = :leaseId")
  Optional<Lease> findById(@Param("leaseId") long leaseId);

  List<Lease> findByOfficeIdAndLeaseStartDateBetweenAndLeaseStatusInOrderByLeaseStartDate(Long officeId, LocalDate startDate, LocalDate endDate, List<LeaseStatus> leaseStatus);

  List<Lease> findByOfficeInAndLeaseStartDateBetweenAndLeaseStatusInOrderByLeaseStartDate(List<Office> offices, LocalDate startDate, LocalDate endDate, List<LeaseStatus> leaseStatus);

  int countByOfficeIdAndLeaseStatusInAndLeaseEndDateGreaterThanEqualAndLeaseStartDateLessThanEqualOrderByLeaseStartDate(
      Long officeId, List<LeaseStatus> leaseStatus, LocalDate startDate, LocalDate endDate);

  int countByOfficeIdAndLeaseStatus(Long officeId, LeaseStatus leaseStatus);

  int countByOfficeInAndLeaseStatus(List<Office> offices,
      LeaseStatus leaseStatus);
}