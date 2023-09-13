package com.dokkebi.officefinder.repository.bookmark;

import com.dokkebi.officefinder.entity.bookmark.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkRepositoryCustom {

  Page<Bookmark> findByCustomerId(long customerId, Pageable pageable);
  void deleteAllByCustomerId(Long customerId);
}
