package com.dokkebi.officefinder.entity.review;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lease_id")
  private Lease lease;

  @Column(name = "review_rating")
  private int rate;
  private String description;

  @Builder
  private Review(Long id, Lease lease, int rate, String description) {
    this.id = id;
    this.lease = lease;
    this.rate = rate;
    this.description = description;
  }

}