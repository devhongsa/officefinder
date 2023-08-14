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
import javax.persistence.OneToOne;
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

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "office_condition_id")
  private OfficeCondition officeCondition;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "office_location_id")
  private OfficeLocation officeLocation;

  @Column(name = "lease_fee")
  private long leaseFee;

  @Column(name = "maximum_capacity")
  private int maxCapacity;

  @Builder
  private Office(Long id, String name, OfficeOwner owner, OfficeCondition officeCondition,
      OfficeLocation officeLocation, long leaseFee, int maxCapacity) {
    this.id = id;
    this.name = name;
    this.owner = owner;
    this.officeCondition = officeCondition;
    this.officeLocation = officeLocation;
    this.leaseFee = leaseFee;
    this.maxCapacity = maxCapacity;
  }

  /*
  엔티티 생성 메서드
   */
  public static Office fromRequestDto(){
    return null;
  }
}
