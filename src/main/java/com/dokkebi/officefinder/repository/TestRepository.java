package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity, Long> {

  boolean existsById(Long id);
}
