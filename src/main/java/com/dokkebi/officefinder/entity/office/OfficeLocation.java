package com.dokkebi.officefinder.entity.office;

import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.type.Address;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OfficeLocation extends BaseEntity {

  @Id
  @Column(name = "office_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "office_id")
  private Office office;

  @Embedded
  private Address address;

  private double latitude;
  private double longitude;

  @Builder
  private OfficeLocation(Long id, Office office, Address address, double latitude,
      double longitude) {
    this.id = id;
    this.office = office;
    this.address = address;
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
