package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.office.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {

}