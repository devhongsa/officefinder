package com.dokkebi.officefinder.repository.office;

import com.dokkebi.officefinder.entity.office.Office;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeRepository extends JpaRepository<Office, Long>, OfficeRepositoryCustom {

}
