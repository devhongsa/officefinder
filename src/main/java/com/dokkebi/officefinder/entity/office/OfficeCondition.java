package com.dokkebi.officefinder.entity.office;

import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.type.AvailableStatus;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class OfficeCondition extends BaseEntity {

  @Id
  @Column(name = "office_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "office_id")
  private Office office;

  @Column(name = "air_conditioner_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus airCondition;

  @Column(name = "cafe_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus cafe;

  @Column(name = "printer_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus printer;

  @Column(name = "package_send_service_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus packageSendService;

  @Column(name = "door_lock_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus doorLock;

  @Column(name = "fax_available_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus fax;

  @Column(name = "public_kitchen_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus publicKitchen;

  @Column(name = "public_Lounge_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus publicLounge;

  @Column(name = "private_locker_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus privateLocker;

  @Column(name = "tv_projector_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus tvProjector;

  @Column(name = "whiteboard_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus whiteboard;

  @Column(name = "wifi_available_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus wifi;

  @Column(name = "shower_available_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus showBooth;

  @Column(name = "storage_available_status")
  @Enumerated(EnumType.STRING)
  private AvailableStatus storage;

  @Builder
  private OfficeCondition(Long id, Office office, AvailableStatus airCondition, AvailableStatus cafe,
      AvailableStatus printer, AvailableStatus packageSendService, AvailableStatus doorLock,
      AvailableStatus fax, AvailableStatus publicKitchen, AvailableStatus publicLounge,
      AvailableStatus privateLocker, AvailableStatus tvProjector, AvailableStatus whiteboard,
      AvailableStatus wifi, AvailableStatus showBooth, AvailableStatus storage) {
    this.id = id;
    this.office = office;
    this.airCondition = airCondition;
    this.cafe = cafe;
    this.printer = printer;
    this.packageSendService = packageSendService;
    this.doorLock = doorLock;
    this.fax = fax;
    this.publicKitchen = publicKitchen;
    this.publicLounge = publicLounge;
    this.privateLocker = privateLocker;
    this.tvProjector = tvProjector;
    this.whiteboard = whiteboard;
    this.wifi = wifi;
    this.showBooth = showBooth;
    this.storage = storage;
  }

  /*
  오피스 상태 변경
   */
  public void changeOfficeCondition(){
    return;
  }
}
