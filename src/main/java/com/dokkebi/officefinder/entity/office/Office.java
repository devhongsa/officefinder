package com.dokkebi.officefinder.entity.office;

import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeModifyRequestDto;
import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.OfficeOwner;
import javax.persistence.CascadeType;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

  @OneToOne(mappedBy = "office", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
  private OfficeCondition officeCondition;

  @OneToOne(mappedBy = "office", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
  private OfficeLocation officeLocation;

  @Column(name = "lease_fee")
  private long leaseFee;

  @Column(name = "maximum_capacity")
  private int maxCapacity;

  @Column(name = "office_address")
  private String officeAddress;

  @Column(name = "office_remain_room")
  private int remainRoom;

  /*
  엔티티 생성 메서드
   */
  public static Office createFromRequest(OfficeCreateRequestDto request, OfficeOwner officeOwner) {
    return Office.builder()
        .name(request.getOfficeName())
        .owner(officeOwner)
        .maxCapacity(request.getMaxCapacity())
        .leaseFee(request.getLeaseFee())
        .remainRoom(request.getRemainRoom())
        .officeAddress(request.getAddress().getStreet())
        .build();
  }

  /*
  엔티티 변경 메서드
   */
  public void modifyFromRequest(OfficeModifyRequestDto request) {

    this.name = request.getOfficeName();
    this.leaseFee = request.getLeaseFee();
    this.maxCapacity = request.getMaxCapacity();
    this.remainRoom = request.getRemainRoom();
  }

  public void setOfficeCondition(OfficeCondition officeCondition) {
    this.officeCondition = officeCondition;
  }

  public void setOfficeLocation(OfficeLocation officeLocation) {
    this.officeLocation = officeLocation;
  }

  public void setOfficeAddress(String address){
    this.officeAddress = address;
  }

  public void decreaseRemainRoom() {
    if (this.remainRoom <= 0){
      throw new IllegalArgumentException("room is full");
    }

    this.remainRoom--;
  }
}
