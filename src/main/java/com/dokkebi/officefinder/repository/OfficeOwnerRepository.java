package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.OfficeOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeOwnerRepository extends JpaRepository<OfficeOwner, Long> {
  boolean existsByEmail(String email);

}
