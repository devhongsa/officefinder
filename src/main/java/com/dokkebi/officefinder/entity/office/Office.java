package com.dokkebi.officefinder.entity.office;

import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.OfficeOwner;
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
public class Office extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "office_id")
  private Long id;

  @Column(name = "office_name", length = 20)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private OfficeOwner owner;

  @Column(name = "lease_fee")
  private long leaseFee;

  @Column(name = "maximum_capacity")
  private int maxCapacity;

  @Builder
  private Office(Long id, String name, OfficeOwner owner, long leaseFee, int maxCapacity) {
    this.id = id;
    this.name = name;
    this.owner = owner;
    this.leaseFee = leaseFee;
    this.maxCapacity = maxCapacity;
  }
}
