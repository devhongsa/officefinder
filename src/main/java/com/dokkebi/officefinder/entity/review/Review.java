package com.dokkebi.officefinder.entity.review;

import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.office.Office;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "office_id")
  private Office office;

  @Column(name = "review_rating")
  private int rate;
  private String description;

  @Builder
  private Review(Long id, Customer customer, Office office, int rate, String description) {
    this.id = id;
    this.customer = customer;
    this.office = office;
    this.rate = rate;
    this.description = description;
  }

  /*
  review 수정 메서드
   */
  public void changeReviewData(){
    return;
  }
}
