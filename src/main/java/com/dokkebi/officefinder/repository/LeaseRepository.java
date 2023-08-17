package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.lease.Lease;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LeaseRepository extends JpaRepository<Lease, Long> {

  @Query("select l from Lease l join fetch l.customer join fetch l.office where l.id = :leaseId")
  Optional<Lease> findByLeaseId(Long leaseId);
}