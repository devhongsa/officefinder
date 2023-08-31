package com.dokkebi.officefinder.entity.review;

import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.lease.Lease;
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

  @Column(name = "customer_id")
  private Long customerId;

  @Column(name = "review_rating")
  private int rate;
  private String description;

  public Review(Long id, Lease lease, Long officeId, Long customerId, int rate,
      String description) {
    this.id = id;
    this.lease = lease;
    this.officeId = officeId;
    this.customerId = customerId;
    this.rate = rate;
    this.description = description;
  }

  public void updateReview(int rate, String description) {
    this.rate = rate;
    this.description = description;
  }

}