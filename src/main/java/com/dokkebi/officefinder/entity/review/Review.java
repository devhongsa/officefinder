package com.dokkebi.officefinder.entity.review;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.SubmitControllerRequest;
import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lease_id")
  private Lease lease;

  @Column(name = "office_id")
  private Long officeId;

  @Column(name = "office_name")
  private String officeName;

  @Column(name = "customer_id")
  private Long customerId;

  @Column(name = "customer_name")
  private String customerName;

  @Column(name = "review_rating")
  private int rate;

  @Column(name = "description")
  private String description;

  public Review(Long id, Lease lease, Long officeId, String officeName, Long customerId,
      String customerName, int rate, String description) {
    this.id = id;
    this.lease = lease;
    this.officeId = officeId;
    this.officeName = officeName;
    this.customerId = customerId;
    this.customerName = customerName;
    this.rate = rate;
    this.description = description;
  }

  public void updateReview(int rate, String description) {
    this.rate = rate;
    this.description = description;
  }

  public static Review from(Lease lease, Long customerId, SubmitControllerRequest reviewRequest) {
    Customer customer = lease.getCustomer();
    Office office = lease.getOffice();

    return Review.builder()
        .lease(lease)
        .customerId(customerId)
        .customerName(customer.getName())
        .officeId(office.getId())
        .officeName(office.getName())
        .rate(reviewRequest.getRate())
        .description(reviewRequest.getDescription())
        .build();
  }

}