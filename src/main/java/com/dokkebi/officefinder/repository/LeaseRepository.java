package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.lease.Lease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaseRepository extends JpaRepository<Lease, Long> {

}
