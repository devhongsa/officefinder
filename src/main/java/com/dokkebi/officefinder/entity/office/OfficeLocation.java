package com.dokkebi.officefinder.entity.office;

import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.type.Address;
import com.dokkebi.officefinder.service.office.dto.OfficeLocationDto;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OfficeLocation extends BaseEntity {

  @Id
  @Column(name = "office_location_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  private Address address;

  private double latitude;
  private double longitude;

  @Builder
  private OfficeLocation(Long id, Address address, double latitude,
      double longitude) {
    this.id = id;
    this.address = address;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /*
  엔티티 생성 메서드
   */
  public static OfficeLocation createFromRequest(OfficeLocationDto request) {
    Address address = Address.fromRequestDto(request);

    return OfficeLocation.builder()
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .address(address)
        .build();
  }

  public void modifyFromRequest(OfficeLocationDto request) {
    this.address = Address.fromRequestDto(request);
    this.latitude = request.getLatitude();
    this.longitude = request.getLongitude();
  }
}
