package com.dokkebi.officefinder.entity.type;

import com.dokkebi.officefinder.service.office.dto.OfficeLocationDto;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

  private String legion;
  private String city;
  private String town;
  private String detail;
  private int zipcode;

  @Builder
  private Address(String legion, String city, String town, String detail, int zipcode) {
    this.legion = legion;
    this.city = city;
    this.town = town;
    this.detail = detail;
    this.zipcode = zipcode;
  }

  /*
  주소 객체 생성 메서드
   */
  public static Address fromRequestDto(OfficeLocationDto request){
    return Address.builder()
        .legion(request.getLegion())
        .city(request.getCity())
        .town(request.getTown())
        .detail(request.getDetail())
        .zipcode(request.getZipcode())
        .build();
  }
}
