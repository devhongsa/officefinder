package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.review.Review;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  boolean existsByLeaseId(Long leaseId);

  List<Review> findByOfficeId(Long officeId);

  Page<Review> findByCustomerId(Long customerId, Pageable pageable);

  Page<Review> findByOfficeId(Long officeId, Pageable pageable);

  List<Review> findTop2ByOfficeIdOrderByCreatedAtDesc(Long officeId);
}