package com.dokkebi.officefinder.controller.bookmark;

import com.dokkebi.officefinder.controller.bookmark.dto.BookmarkDto;
import com.dokkebi.officefinder.controller.bookmark.dto.SubmitDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
@PreAuthorize("hasRole('CUSTOMER')")
public class BookmarkController {

  private final TokenProvider tokenProvider;
  private final BookmarkService bookmarkService;
  private final OfficePictureRepository officePictureRepository;

  @PostMapping("/submit")
  public ResponseDto<Long> submitBookmark(@RequestHeader("Authorization") String jwt,
      @RequestBody @Valid SubmitDto request) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwt);
    bookmarkService.submitBookmark(customerId, request.getOfficeId());

    return new ResponseDto<>("success", request.getOfficeId());
  }

  @GetMapping
  public Page<BookmarkDto> getBookmarks(@RequestHeader("Authorization") String jwt,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwt);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<Bookmark> bookmarks = bookmarkService.getBookmarks(customerId, pageable);

    return bookmarks.map(content -> BookmarkDto.from(content,
        officePictureRepository.findByOfficeId(content.getOffice().getId())));
  }

  @DeleteMapping("/{bookmarkId}")
  public ResponseDto<Long> deleteBookmark(@RequestHeader("Authorization") String jwt,
      @PathVariable("bookmarkId") Long bookmarkId) {
    Long customerId = tokenProvider.getUserIdFromHeader(jwt);
    bookmarkService.deleteBookmark(bookmarkId);

    return new ResponseDto<>("success", bookmarkId);
  }

  @DeleteMapping
  public void deleteAllBookmark(@RequestHeader("Authorization") String jwt){
    bookmarkService.deleteAllBookMark(tokenProvider.getUserIdFromHeader(jwt));
  }

}