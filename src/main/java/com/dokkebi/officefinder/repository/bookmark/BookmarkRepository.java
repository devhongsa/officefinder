package com.dokkebi.officefinder.repository.bookmark;

import com.dokkebi.officefinder.entity.bookmark.Bookmark;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  Optional<Bookmark> findByCustomerIdAndOfficeId(Long customerId, Long officeId);

  Page<Bookmark> findByCustomerId(Long customerId, Pageable pageable);
}