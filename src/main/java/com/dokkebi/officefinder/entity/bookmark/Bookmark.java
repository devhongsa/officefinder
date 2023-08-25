package com.dokkebi.officefinder.entity.bookmark;

import com.dokkebi.officefinder.controller.bookmark.dto.BookmarkDto;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.office.Office;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "book_mark")
@Getter
@Builder
@NoArgsConstructor
public class Bookmark {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bookmark_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne
  @JoinColumn(name = "office_id")
  private Office office;

  public Bookmark(Long id, Customer customer, Office office) {
    this.id = id;
    this.customer = customer;
    this.office = office;
  }

  public static BookmarkDto toDto(Bookmark bookmark) {
    Office office = bookmark.getOffice();
    return BookmarkDto.builder()
        .name(office.getName())
        .officeAddress(office.getOfficeAddress())
        .build();
  }
}