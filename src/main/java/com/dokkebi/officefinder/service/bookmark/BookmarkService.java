package com.dokkebi.officefinder.service.bookmark;

import static com.dokkebi.officefinder.exception.CustomErrorCode.USER_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.bookmark.Bookmark;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.bookmark.BookmarkRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final CustomerRepository customerRepository;
  private final OfficeRepository officeRepository;


  public Bookmark submitBookmark(Long customerId, Long officeId) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND, USER_NOT_FOUND.getErrorMessage(),
            BAD_REQUEST));
    Office office = officeRepository.findById(officeId)
        .orElseThrow(() -> new IllegalArgumentException("해당 오피스는 존재하지 않습니다."));
    Bookmark bookmark = Bookmark.builder()
        .customer(customer)
        .office(office)
        .build();
    return bookmarkRepository.save(bookmark);
  }

  public Page<Bookmark> getBookmarks(Long customerId, Pageable pageable) {
    return bookmarkRepository.findByCustomerId(customerId, pageable);
  }

  public void deleteBookmark(Long customerId, Long officeId) {
    Bookmark bookmark = bookmarkRepository.findByCustomerIdAndOfficeId(customerId, officeId)
        .orElseThrow(() -> new IllegalArgumentException("북마크가 존재하지 않습니다."));
    bookmarkRepository.delete(bookmark);
  }
}