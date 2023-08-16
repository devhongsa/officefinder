package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.OfficeOwner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeOwnerRepository extends JpaRepository<OfficeOwner, Long> {
  boolean existsByEmail(String email);

  Optional<OfficeOwner> findByEmail(String ownerEmail);
}
