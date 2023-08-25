package com.dokkebi.officefinder.repository.lease;

import com.dokkebi.officefinder.entity.lease.Lease;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeaseRepository extends JpaRepository<Lease, Long>, LeaseRepositoryCustom {

  @Query("select l from Lease l join fetch l.customer join fetch l.office where l.id = :leaseId")
  Optional<Lease> findById(@Param("leaseId") Long leaseId);

//  @Query("select l from Lease l join fetch l.customer where l.office = :office "
//      + "and l.leaseStatus = :leaseStatus")
//  Page<Lease> findByOfficeAndLeaseStatus(Office office, LeaseStatus leaseStatus, Pageable pageable);
}