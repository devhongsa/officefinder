package com.dokkebi.officefinder.controller.bookmark;

import com.dokkebi.officefinder.controller.bookmark.dto.BookmarkDto;
import com.dokkebi.officefinder.controller.customer.dto.PointChargeHistoryDto;
import com.dokkebi.officefinder.dto.PageInfo;
import com.dokkebi.officefinder.dto.PageResponseDto;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.entity.PointChargeHistory;
import com.dokkebi.officefinder.entity.bookmark.Bookmark;
import com.dokkebi.officefinder.security.TokenProvider;
import com.dokkebi.officefinder.service.bookmark.BookmarkService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

  private final TokenProvider tokenProvider;
  private final BookmarkService bookmarkService;
  @PostMapping("/submit")
  public ResponseDto<?> submitBookmark(@RequestHeader("Authorization") String jwtHeader,
      @RequestBody @Valid Long officeId) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwtHeader);
    bookmarkService.submitBookmark(customerId, officeId);

    return new ResponseDto<>("success", customerId);
  }

  @GetMapping
  public PageResponseDto<?> getBookmarks(@RequestHeader("Authorization") String jwtHeader,
      Pageable pageable) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwtHeader);
    Page<Bookmark> bookmarks = bookmarkService.getBookmarks(customerId, pageable);

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) bookmarks.getTotalElements(), bookmarks.getTotalPages());

    List<BookmarkDto> list = bookmarks.stream().map(Bookmark::toDto)
        .collect(Collectors.toList());

    return new PageResponseDto<>(list, pageInfo);
  }

  @PostMapping("/delete")
  public ResponseDto<?> deleteBookmark(@RequestHeader("Authorization") String jwtHeader,
      @RequestBody @Valid Long officeId) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwtHeader);
    bookmarkService.deleteBookmark(customerId, officeId);

    return new ResponseDto<>("success", customerId);
  }

}