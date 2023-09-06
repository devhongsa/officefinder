package com.dokkebi.officefinder.controller.bookmark;

import com.dokkebi.officefinder.controller.bookmark.dto.BookmarkDto;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.entity.bookmark.Bookmark;
import com.dokkebi.officefinder.repository.office.picture.OfficePictureRepository;
import com.dokkebi.officefinder.security.TokenProvider;
import com.dokkebi.officefinder.service.bookmark.BookmarkService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

  private final TokenProvider tokenProvider;
  private final BookmarkService bookmarkService;
  private final OfficePictureRepository officePictureRepository;

  @PreAuthorize("hasRole('CUSTOMER')")
  @PostMapping("/submit")
  public ResponseDto<Long> submitBookmark(@RequestHeader("Authorization") String jwtHeader,
      @RequestBody @Valid Long officeId) {

    Long customerId = tokenProvider.getUserIdFromHeader(jwtHeader);
    bookmarkService.submitBookmark(customerId, officeId);

    return new ResponseDto<>("success", officeId);
  }

  @PreAuthorize("hasRole('CUSTOMER')")
  @GetMapping
  public Page<BookmarkDto> getBookmarks(@RequestHeader("Authorization") String jwtHeader,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwtHeader);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<Bookmark> bookmarks = bookmarkService.getBookmarks(customerId, pageable);

    return bookmarks.map(content -> BookmarkDto.from(content,
        officePictureRepository.findByOfficeId(content.getOffice().getId())));
  }

  @PreAuthorize("hasRole('CUSTOMER')")
  @DeleteMapping("/delete")
  public ResponseDto<Long> deleteBookmark(@RequestHeader("Authorization") String jwtHeader,
      @RequestBody @Valid Long officeId) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwtHeader);
    bookmarkService.deleteBookmark(customerId, officeId);

    return new ResponseDto<>("success", officeId);
  }

}