package com.dokkebi.officefinder.service.office;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeModifyRequestDto;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficeCondition;
import com.dokkebi.officefinder.entity.office.OfficeLocation;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.repository.office.condition.OfficeConditionRepository;
import com.dokkebi.officefinder.repository.office.location.OfficeLocationRepository;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class OfficeServiceTest {

  @Autowired
  private OfficeService officeService;
  @Autowired
  private OfficeOwnerRepository officeOwnerRepository;
  @Autowired
  private OfficeRepository officeRepository;
  @Autowired
  private OfficeConditionRepository officeConditionRepository;
  @Autowired
  private OfficeLocationRepository officeLocationRepository;
  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  private static final String REMAIN_ROOM_KEY = "remain-room";

  @AfterEach
  void tearDown() {
    redisTemplate.delete(REMAIN_ROOM_KEY);
  }

  @DisplayName("오피스 정보를 생성할 수 있다. 생성된 오피스 정보는 DB에 저장된다.")
  @Test
  public void createOfficeInfoTest() throws Exception {
    // given
    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));

    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);

    OfficeCreateRequestDto request = new OfficeCreateRequestDto();
    setOfficeInfo(request, "office1", 5, 500000, 5);
    setOfficeLocation(request, "경상남도", "김해시", "삼계동", "", "삼계로", 12345, 127.1315, 37.4562);
    setOfficeCondition(request, false, false, true, true, true, true,
        true, true, true, true, true, true, true, true, true);

    // when
    Long savedId = officeService.createOfficeInfo(request, savedOfficeOwner.getEmail());

    // then
    Office result = officeRepository.findByOfficeId(savedId)
        .orElseThrow(() -> new IllegalArgumentException("해당 오피스가 존재하지 않습니다."));

    OfficeCondition resultOfficeCondition = result.getOfficeCondition();
    OfficeLocation resultOfficeLocation = result.getOfficeLocation();

    assertThat(result)
        .extracting("name", "maxCapacity", "leaseFee", "officeAddress")
        .contains(
            "office1", 5, 500000L, "경상남도 김해시 삼계동 삼계로"
        );

    assertThat(resultOfficeLocation)
        .extracting(OfficeLocation::getAddress)
        .extracting("legion", "city", "town", "village", "street", "zipcode")
        .contains(
            "경상남도", "김해시", "삼계동", "", "삼계로", 12345
        );

    assertThat(resultOfficeLocation)
        .extracting("latitude", "longitude")
        .contains(127.1315, 37.4562);

    assertThat(resultOfficeCondition)
        .extracting("airCondition", "heaterCondition", "cafe", "printer", "packageSendService",
            "doorLock", "fax", "publicKitchen", "publicLounge", "privateLocker", "tvProjector",
            "whiteboard", "wifi", "showerBooth", "storage")
        .contains(
            false, false, true, true, true, true, true, true, true, true, true, true, true, true,
            true
        );
  }

  @DisplayName("오피스 정보를 수정할 수 있다.")
  @Test
  public void modifyOfficeInfoTest() throws Exception {
    // given
    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));

    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);

    // set office create request dto
    OfficeCreateRequestDto request = new OfficeCreateRequestDto();
    setOfficeInfo(request, "office1", 5, 500000, 5);
    setOfficeLocation(request, "경상남도", "김해시", "삼계동", "", "삼계로", 12345, 127.1315, 37.4562);
    setOfficeCondition(request, false, false, true, true, true, true,
        true, true, true, true, true, true, true, true, true);

    Long savedId = officeService.createOfficeInfo(request, savedOfficeOwner.getEmail());

    // set office modify request dto
    OfficeModifyRequestDto modifyRequest = new OfficeModifyRequestDto();
    setOfficeInfo(modifyRequest, "office1", 5, 1000000, 5);
    setOfficeLocation(modifyRequest, "경상남도", "김해시", "삼계동", "", "삼계로", 12345, 127.1315, 37.4562);
    setOfficeCondition(modifyRequest, true, true, true, true, true, true,
        true, true, true, true, true, true, true, true, true);

    // when
    Long modifiedOfficeId = officeService.modifyOfficeInfo(modifyRequest,
        savedOfficeOwner.getEmail(), savedId);

    // then
    Office result = officeRepository.findByOfficeId(modifiedOfficeId)
        .orElseThrow(() -> new IllegalArgumentException("해당 오피스가 존재하지 않습니다."));

    OfficeCondition resultOfficeCondition = result.getOfficeCondition();
    OfficeLocation resultOfficeLocation = result.getOfficeLocation();

    assertThat(result)
        .extracting("name", "maxCapacity", "leaseFee", "officeAddress")
        .contains(
            "office1", 5, 1000000L, "경상남도 김해시 삼계동 삼계로"
        );

    assertThat(resultOfficeLocation)
        .extracting(OfficeLocation::getAddress)
        .extracting("legion", "city", "town", "village", "street", "zipcode")
        .contains(
            "경상남도", "김해시", "삼계동", "", "삼계로", 12345
        );

    assertThat(resultOfficeLocation)
        .extracting("latitude", "longitude")
        .contains(127.1315, 37.4562);

    assertThat(resultOfficeCondition)
        .extracting("airCondition", "heaterCondition", "cafe", "printer", "packageSendService",
            "doorLock", "fax", "publicKitchen", "publicLounge", "privateLocker", "tvProjector",
            "whiteboard", "wifi", "showerBooth", "storage")
        .contains(
            true, true, true, true, true, true, true, true, true, true, true, true, true, true,
            true
        );
  }

  @DisplayName("오피스의 주인이 아닌 인원이 오피스 정보를 수정하려고 할 시 예외가 발생한다.")
  @Test
  public void modifyOfficeInfoTestWithAnotherOwner() {
    // given
    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));

    OfficeOwner officeOwner2 = createOfficeOwner("park", "owner2@test.com", "12345", "123-45",
        1000L,
        Set.of("ROLE_OFFICE_OWNER"));

    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);
    OfficeOwner savedOfficeOwner2 = officeOwnerRepository.save(officeOwner2);

    // set office create request dto
    OfficeCreateRequestDto request = new OfficeCreateRequestDto();
    setOfficeInfo(request, "office1", 5, 500000, 5);
    setOfficeLocation(request, "경상남도", "김해시", "삼계동", "", "삼계로", 12345, 127.1315, 37.4562);
    setOfficeCondition(request, false, false, true, true, true, true,
        true, true, true, true, true, true, true, true, true);

    Long savedId = officeService.createOfficeInfo(request, savedOfficeOwner.getEmail());

    // set office modify request dto
    OfficeModifyRequestDto modifyRequest = new OfficeModifyRequestDto();
    setOfficeInfo(modifyRequest, "office1", 5, 1000000, 5);
    setOfficeLocation(modifyRequest, "경상남도", "김해시", "삼계동", "", "삼계로", 12345, 127.1315, 37.4562);
    setOfficeCondition(modifyRequest, true, true, true, true, true, true,
        true, true, true, true, true, true, true, true, true);

    // when
    // then
    assertThatThrownBy(
        () -> officeService.modifyOfficeInfo(modifyRequest, savedOfficeOwner2.getEmail(), savedId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("잘못된 접근입니다.");
  }

  @DisplayName("오피스 정보를 삭제할 수 있다.")
  @Test
  public void deleteOfficeInfoTest() {
    // given
    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));

    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);

    OfficeCreateRequestDto request = new OfficeCreateRequestDto();
    setOfficeInfo(request, "office1", 5, 500000, 5);
    setOfficeLocation(request, "경상남도", "김해시", "삼계동", "", "삼계로", 12345, 127.1315, 37.4562);
    setOfficeCondition(request, false, false, true, true, true, true,
        true, true, true, true, true, true, true, true, true);

    Long savedId = officeService.createOfficeInfo(request, savedOfficeOwner.getEmail());

    // when
    officeService.deleteOfficeInfo(savedId);

    // then
    boolean officeStatus = officeRepository.findById(savedId).isEmpty();
    boolean officeLocationStatus = officeLocationRepository.findAll().isEmpty();
    boolean officeConditionStatus = officeConditionRepository.findAll().isEmpty();

    assertThat(officeStatus).isTrue();
    assertThat(officeLocationStatus).isTrue();
    assertThat(officeConditionStatus).isTrue();
  }

  private OfficeOwner createOfficeOwner(String name, String email, String password,
      String businessNumber, long point, Set<String> roles) {

    return OfficeOwner.builder()
        .name(name)
        .email(email)
        .password(password)
        .businessNumber(businessNumber)
        .point(point)
        .roles(roles)
        .build();
  }

  private void setOfficeInfo(OfficeCreateRequestDto request, String officeName, int maxCapacity,
      long leaseFee, int remainRoom) {
    request.setOfficeName(officeName);
    request.setMaxCapacity(maxCapacity);
    request.setLeaseFee(leaseFee);
    request.setRemainRoom(remainRoom);
  }

  private void setOfficeLocation(OfficeCreateRequestDto request, String legion, String city,
      String town, String village, String street, int zipcode, double latitude, double longitude) {

    request.setLegion(legion);
    request.setCity(city);
    request.setTown(town);
    request.setVillage(village);
    request.setStreet(street);
    request.setZipcode(zipcode);
    request.setLatitude(latitude);
    request.setLongitude(longitude);
  }

  private void setOfficeCondition(OfficeCreateRequestDto request, boolean airCondition,
      boolean heaterCondition, boolean cafe,
      boolean printer, boolean packageSendService, boolean doorLock, boolean fax,
      boolean publicKitchen, boolean publicLounge, boolean privateLocker, boolean tvProjector,
      boolean whiteboard, boolean wifi, boolean showerBooth, boolean storage) {

    request.setHaveAirCondition(airCondition);
    request.setHaveHeater(heaterCondition);
    request.setHaveCafe(cafe);
    request.setHavePrinter(printer);
    request.setPackageSendServiceAvailable(packageSendService);
    request.setHaveDoorLock(doorLock);
    request.setFaxServiceAvailable(fax);
    request.setHavePublicKitchen(publicKitchen);
    request.setHavePublicLounge(publicLounge);
    request.setHavePrivateLocker(privateLocker);
    request.setHaveTvProjector(tvProjector);
    request.setHaveWhiteBoard(whiteboard);
    request.setHaveWifi(wifi);
    request.setHaveShowerBooth(showerBooth);
    request.setHaveStorage(storage);
  }

  private void setOfficeInfo(OfficeModifyRequestDto request, String officeName, int maxCapacity,
      long leaseFee, int remainRoom) {
    request.setOfficeName(officeName);
    request.setMaxCapacity(maxCapacity);
    request.setLeaseFee(leaseFee);
    request.setRemainRoom(remainRoom);
  }

  private void setOfficeLocation(OfficeModifyRequestDto request, String legion, String city,
      String town, String village, String street, int zipcode, double latitude, double longitude) {

    request.setLegion(legion);
    request.setCity(city);
    request.setTown(town);
    request.setVillage(village);
    request.setStreet(street);
    request.setZipcode(zipcode);
    request.setLatitude(latitude);
    request.setLongitude(longitude);
  }

  private void setOfficeCondition(OfficeModifyRequestDto request, boolean airCondition,
      boolean heaterCondition, boolean cafe,
      boolean printer, boolean packageSendService, boolean doorLock, boolean fax,
      boolean publicKitchen, boolean publicLounge, boolean privateLocker, boolean tvProjector,
      boolean whiteboard, boolean wifi, boolean showerBooth, boolean storage) {

    request.setHaveAirCondition(airCondition);
    request.setHaveHeater(heaterCondition);
    request.setHaveCafe(cafe);
    request.setHavePrinter(printer);
    request.setPackageSendServiceAvailable(packageSendService);
    request.setHaveDoorLock(doorLock);
    request.setFaxServiceAvailable(fax);
    request.setHavePublicKitchen(publicKitchen);
    request.setHavePublicLounge(publicLounge);
    request.setHavePrivateLocker(privateLocker);
    request.setHaveTvProjector(tvProjector);
    request.setHaveWhiteBoard(whiteboard);
    request.setHaveWifi(wifi);
    request.setHaveShowerBooth(showerBooth);
    request.setHaveStorage(storage);
  }

}