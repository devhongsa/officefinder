package com.dokkebi.officefinder.service.bookmark;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.bookmark.Bookmark;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.bookmark.BookmarkRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BookmarkServiceTest {

  @Autowired
  private BookmarkService bookmarkService;
  @Autowired
  private BookmarkRepository bookmarkRepository;
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private OfficeRepository officeRepository;
  @DisplayName("CustomerId와 OfficeId로 북마크를 등록할 수 있다.")
  @Test
  void submitBookmark() {
    //given
    Customer cus = Customer.builder()
        .id(1L).name("test").email("test@test").password("test").point(0L).roles(Set.of("a"))
        .build();
    Office office = Office.builder()
        .id(1L).name("test")
        .build();
    Customer savedC = customerRepository.save(cus);
    Office savedO = officeRepository.save(office);
    //when
    Bookmark bookmark = bookmarkService.submitBookmark(savedC.getId(), savedO.getId());
    //then
    Assertions.assertThat(bookmark.getCustomer().getId()).isEqualTo(savedC.getId());
    Assertions.assertThat(bookmark.getOffice().getId()).isEqualTo(savedO.getId());
  }
}