package com.dokkebi.officefinder.repository.office;

import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.office.Office;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeRepository extends JpaRepository<Office, Long>, OfficeRepositoryCustom {
  Optional<Office> findByOwnerAndId(OfficeOwner officeOwner, Long officeId);

  Optional<Office> findByIdAndOwnerId(Long officeId, Long ownerId);

  List<Office> findByOwnerId(Long ownerId);
}
