package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.review.Review;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  boolean existsByLeaseId(Long leaseId);

  Optional<Review> findByLeaseId(Long leaseId);
}