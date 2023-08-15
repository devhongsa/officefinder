package com.dokkebi.officefinder.entity.office;

import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.service.office.dto.OfficeConditionDto;
import javax.persistence.Column;
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
public class OfficeCondition extends BaseEntity {

  @Id
  @Column(name = "office_condition_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "air_conditioner_status")
  private boolean airCondition;

  @Column(name = "heater_status")
  private boolean heaterCondition;

  @Column(name = "cafe_status")
  private boolean cafe;

  @Column(name = "printer_status")
  private boolean printer;

  @Column(name = "package_send_service_status")
  private boolean packageSendService;

  @Column(name = "door_lock_status")
  private boolean doorLock;

  @Column(name = "fax_available_status")
  private boolean fax;

  @Column(name = "public_kitchen_status")
  private boolean publicKitchen;

  @Column(name = "public_Lounge_status")
  private boolean publicLounge;

  @Column(name = "private_locker_status")
  private boolean privateLocker;

  @Column(name = "tv_projector_status")
  private boolean tvProjector;

  @Column(name = "whiteboard_status")
  private boolean whiteboard;

  @Column(name = "wifi_available_status")
  private boolean wifi;

  @Column(name = "shower_available_status")
  private boolean showerBooth;

  @Column(name = "storage_available_status")
  private boolean storage;

  @Builder
  private OfficeCondition(Long id, boolean airCondition, boolean heaterCondition, boolean cafe,
      boolean printer, boolean packageSendService, boolean doorLock, boolean fax,
      boolean publicKitchen, boolean publicLounge, boolean privateLocker, boolean tvProjector,
      boolean whiteboard, boolean wifi, boolean showerBooth, boolean storage) {
    this.id = id;
    this.airCondition = airCondition;
    this.heaterCondition = heaterCondition;
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
    this.showerBooth = showerBooth;
    this.storage = storage;
  }

  /*
  엔티티 생성 메서드
   */
  public static OfficeCondition createFromRequest(OfficeConditionDto request) {
    return OfficeCondition.builder()
        .airCondition(request.isHaveAirCondition())
        .heaterCondition(request.isHaveHeater())
        .cafe(request.isHaveCafe())
        .printer(request.isHavePrinter())
        .packageSendService(request.isPackageSendServiceAvailable())
        .doorLock(request.isHaveDoorLock())
        .fax(request.isFaxServiceAvailable())
        .publicKitchen(request.isHavePublicKitchen())
        .publicLounge(request.isHavePublicLounge())
        .privateLocker(request.isHavePrivateLocker())
        .tvProjector(request.isHaveTvProjector())
        .whiteboard(request.isHaveWhiteBoard())
        .wifi(request.isHaveWifi())
        .showerBooth(request.isHaveShowerBooth())
        .storage(request.isHaveStorage())
        .build();
  }

  /*
  엔티티 수정 메서드
   */
  public void modifyFromRequest(OfficeConditionDto request) {

    this.airCondition = request.isHaveAirCondition();
    this.heaterCondition = request.isHaveHeater();
    this.cafe = request.isHaveCafe();
    this.printer = request.isHavePrinter();
    this.packageSendService = request.isPackageSendServiceAvailable();
    this.doorLock = request.isHaveDoorLock();
    this.fax = request.isFaxServiceAvailable();
    this.publicKitchen = request.isHavePublicKitchen();
    this.publicLounge = request.isHavePublicLounge();
    this.privateLocker = request.isHavePrivateLocker();
    this.tvProjector = request.isHaveTvProjector();
    this.whiteboard = request.isHaveWhiteBoard();
    this.wifi = request.isHaveWifi();
    this.showerBooth = request.isHaveShowerBooth();
    this.storage = request.isHaveStorage();
  }
}
