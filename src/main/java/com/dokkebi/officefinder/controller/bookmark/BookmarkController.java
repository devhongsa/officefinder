package com.dokkebi.officefinder.controller.bookmark;

import com.dokkebi.officefinder.controller.bookmark.dto.BookmarkDto;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.entity.bookmark.Bookmark;
import com.dokkebi.officefinder.security.TokenProvider;
import com.dokkebi.officefinder.service.bookmark.BookmarkService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

  private final TokenProvider tokenProvider;
  private final BookmarkService bookmarkService;

  @PreAuthorize("hasRole('CUSTOMER')")
  @PostMapping("/submit")
  public ResponseDto<Long> submitBookmark(@CookieValue("Authorization") String jwt,
      @RequestBody @Valid Long officeId) {
    Long customerId = tokenProvider.getUserId(jwt);
    bookmarkService.submitBookmark(customerId, officeId);

    return new ResponseDto<>("success", officeId);
  }

  @PreAuthorize("hasRole('CUSTOMER')")
  @GetMapping
  public Page<BookmarkDto> getBookmarks(@CookieValue("Authorization") String jwt,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    Long customerId = tokenProvider.getUserId(jwt);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<Bookmark> bookmarks = bookmarkService.getBookmarks(customerId, pageable);

    return bookmarks.map(BookmarkDto::from);
  }

  @PreAuthorize("hasRole('CUSTOMER')")
  @DeleteMapping("/delete")
  public ResponseDto<Long> deleteBookmark(@CookieValue("Authorization") String jwt,
      @RequestBody @Valid Long officeId) {
    Long customerId = tokenProvider.getUserId(jwt);
    bookmarkService.deleteBookmark(customerId, officeId);

    return new ResponseDto<>("success", officeId);
  }

}