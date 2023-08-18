package com.dokkebi.officefinder.service.office;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.dokkebi.officefinder.controller.office.dto.OfficeBasicSearchCond;
import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeDetailSearchCond;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficeLocation;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class OfficeSearchServiceTest {

  @Autowired
  private OfficeService officeService;

  @Autowired
  private OfficeSearchService officeQueryService;
  @Autowired
  private OfficeOwnerRepository officeOwnerRepository;
  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  private static final String REMAIN_ROOM_KEY = "remain-room";

  @AfterEach
  void tearDown() {
    redisTemplate.delete(REMAIN_ROOM_KEY);
  }

  @DisplayName("기본 조건(도 행정구역)으로 오피스를 검색할 수 있다. 검색된 오피스는 페이징 처리가 되어 반환된다.")
  @Test
  public void searchOfficeByBasicConditionTest() {
    // given
    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));

    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);

    addOfficeData(savedOfficeOwner);

    OfficeBasicSearchCond cond = new OfficeBasicSearchCond();
    cond.setLegion("경상남도");

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Office> offices = officeQueryService.searchOfficeByBasicCondition(cond, pageRequest);
    List<Office> content = offices.getContent();

    // then
    assertThat(content).hasSize(4)
        .extracting("name", "leaseFee", "maxCapacity", "officeAddress")
        .containsExactlyInAnyOrder(
            tuple("office1", 500000L, 5, "경상남도 김해시 삼계동 삼계로"),
            tuple("office2", 1000000L, 10, "경상남도 김해시 삼계동 삼계로"),
            tuple("office4", 1500000L, 10, "경상남도 진영시 가츠동 가츠로"),
            tuple("office5", 2000000L, 15, "경상남도 김해시 내외동 내외로")
        );

    assertThat(content)
        .extracting(Office::getOfficeLocation)
        .extracting(OfficeLocation::getAddress)
        .extracting("legion", "city", "town", "village", "street", "zipcode")
        .containsExactlyInAnyOrder(
            tuple("경상남도", "김해시", "삼계동", "", "삼계로", 12345),
            tuple("경상남도", "김해시", "삼계동", "", "삼계로", 12348),
            tuple("경상남도", "진영시", "가츠동", "", "가츠로", 12598),
            tuple("경상남도", "김해시", "내외동", "", "내외로", 12508)
        );

    assertThat(content)
        .extracting(Office::getOfficeLocation)
        .extracting("latitude", "longitude")
        .containsExactlyInAnyOrder(
            tuple(127.1315, 37.4562),
            tuple(127.1324, 37.4500),
            tuple(127.1284, 37.4420),
            tuple(127.1286, 37.4490)
        );

    assertThat(content)
        .extracting(Office::getOfficeCondition)
        .extracting("airCondition", "heaterCondition", "cafe", "printer", "packageSendService",
            "doorLock", "fax", "publicKitchen", "publicLounge", "privateLocker", "tvProjector",
            "whiteboard", "wifi", "showerBooth", "storage")
        .containsExactlyInAnyOrder(
            tuple(false, false, true, true, true, true, true, true, true, true, true, true, true,
                true, true),
            tuple(true, true, false, true, true, true, true, false, false, true, true, true, true,
                true, true),
            tuple(true, true, true, true, true, true, true, true, true, true, true, true, true,
                true, true),
            tuple(true, true, true, true, true, true, true, false, false, true, true, true, true,
                false, true)
        );
  }

  @DisplayName("기본 조건(시, 도 행정구역)으로 오피스를 검색할 수 있다. 검색된 오피스는 페이징 처리가 되어 반환된다.")
  @Test
  public void searchOfficeByBasicConditionTest2() {
    // given
    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));

    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);

    addOfficeData(savedOfficeOwner);

    OfficeBasicSearchCond cond = new OfficeBasicSearchCond();
    cond.setLegion("경상남도");
    cond.setCity("김해시");

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Office> offices = officeQueryService.searchOfficeByBasicCondition(cond, pageRequest);
    List<Office> content = offices.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("name", "leaseFee", "maxCapacity", "officeAddress")
        .containsExactlyInAnyOrder(
            tuple("office1", 500000L, 5, "경상남도 김해시 삼계동 삼계로"),
            tuple("office2", 1000000L, 10, "경상남도 김해시 삼계동 삼계로"),
            tuple("office5", 2000000L, 15, "경상남도 김해시 내외동 내외로")
        );

    assertThat(content)
        .extracting(Office::getOfficeLocation)
        .extracting(OfficeLocation::getAddress)
        .extracting("legion", "city", "town", "village", "street", "zipcode")
        .containsExactlyInAnyOrder(
            tuple("경상남도", "김해시", "삼계동", "", "삼계로", 12345),
            tuple("경상남도", "김해시", "삼계동", "", "삼계로", 12348),
            tuple("경상남도", "김해시", "내외동", "", "내외로", 12508)
        );

    assertThat(content)
        .extracting(Office::getOfficeLocation)
        .extracting("latitude", "longitude")
        .containsExactlyInAnyOrder(
            tuple(127.1315, 37.4562),
            tuple(127.1324, 37.4500),
            tuple(127.1286, 37.4490)
        );

    assertThat(content)
        .extracting(Office::getOfficeCondition)
        .extracting("airCondition", "heaterCondition", "cafe", "printer", "packageSendService",
            "doorLock", "fax", "publicKitchen", "publicLounge", "privateLocker", "tvProjector",
            "whiteboard", "wifi", "showerBooth", "storage")
        .containsExactlyInAnyOrder(
            tuple(false, false, true, true, true, true, true, true, true, true, true, true, true,
                true, true),
            tuple(true, true, false, true, true, true, true, false, false, true, true, true, true,
                true, true),
            tuple(true, true, true, true, true, true, true, false, false, true, true, true, true,
                false, true)
        );
  }

  @DisplayName("기본 조건(시, 도 행정구역, 최대 수용 인원 수)으로 오피스를 검색할 수 있다. 검색된 오피스는 페이징 처리가 되어 반환된다.")
  @Test
  public void searchOfficeByBasicConditionTest3() {
    // given
    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));

    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);

    addOfficeData(savedOfficeOwner);

    OfficeBasicSearchCond cond = new OfficeBasicSearchCond();
    cond.setLegion("경상남도");
    cond.setCity("김해시");
    cond.setMaxCapacity(5);

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Office> offices = officeQueryService.searchOfficeByBasicCondition(cond, pageRequest);
    List<Office> content = offices.getContent();

    // then
    assertThat(content).hasSize(1)
        .extracting("name", "leaseFee", "maxCapacity", "officeAddress")
        .containsExactlyInAnyOrder(
            tuple("office1", 500000L, 5, "경상남도 김해시 삼계동 삼계로")
        );

    assertThat(content)
        .extracting(Office::getOfficeLocation)
        .extracting(OfficeLocation::getAddress)
        .extracting("legion", "city", "town", "village", "street", "zipcode")
        .containsExactlyInAnyOrder(
            tuple("경상남도", "김해시", "삼계동", "", "삼계로", 12345)
        );

    assertThat(content)
        .extracting(Office::getOfficeLocation)
        .extracting("latitude", "longitude")
        .containsExactlyInAnyOrder(
            tuple(127.1315, 37.4562)
        );

    assertThat(content)
        .extracting(Office::getOfficeCondition)
        .extracting("airCondition", "heaterCondition", "cafe", "printer", "packageSendService",
            "doorLock", "fax", "publicKitchen", "publicLounge", "privateLocker", "tvProjector",
            "whiteboard", "wifi", "showerBooth", "storage")
        .containsExactlyInAnyOrder(
            tuple(false, false, true, true, true, true, true, true, true, true, true, true, true,
                true, true)
        );
  }

  @DisplayName("상세 조건(보유시설, 최대 수용 인원)으로 오피스를 검색할 수 있다. 검색된 오피스는 페이징 처리가 되어 반환된다.")
  @Test
  public void searchOfficeByDetailConditionTest() {
    // given
    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));

    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);

    addOfficeData(savedOfficeOwner);

    OfficeDetailSearchCond cond = new OfficeDetailSearchCond();
    cond.setHaveWhiteBoard(true);
    cond.setHavePublicKitchen(true);
    cond.setMaxCapacity(10);

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Office> offices = officeQueryService.searchOfficeByDetailCondition(cond, pageRequest);
    List<Office> content = offices.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("name", "leaseFee", "maxCapacity", "officeAddress")
        .containsExactlyInAnyOrder(
            tuple("office1", 500000L, 5, "경상남도 김해시 삼계동 삼계로"),
            tuple("office3", 1500000L, 10, "부산광역시 동구 좌천동 좌천로"),
            tuple("office4", 1500000L, 10, "경상남도 진영시 가츠동 가츠로")
        );

    assertThat(content)
        .extracting(Office::getOfficeLocation)
        .extracting(OfficeLocation::getAddress)
        .extracting("legion", "city", "town", "village", "street", "zipcode")
        .containsExactlyInAnyOrder(
            tuple("경상남도", "김해시", "삼계동", "", "삼계로", 12345),
            tuple("부산광역시", "동구", "좌천동", "", "좌천로", 12398),
            tuple("경상남도", "진영시", "가츠동", "", "가츠로", 12598)
        );

    assertThat(content)
        .extracting(Office::getOfficeLocation)
        .extracting("latitude", "longitude")
        .containsExactlyInAnyOrder(
            tuple(127.1315, 37.4562),
            tuple(127.1524, 37.4320),
            tuple(127.1284, 37.4420)
        );

    assertThat(content)
        .extracting(Office::getOfficeCondition)
        .extracting("airCondition", "heaterCondition", "cafe", "printer", "packageSendService",
            "doorLock", "fax", "publicKitchen", "publicLounge", "privateLocker", "tvProjector",
            "whiteboard", "wifi", "showerBooth", "storage")
        .containsExactlyInAnyOrder(
            tuple(false, false, true, true, true, true, true, true, true, true, true, true, true,
                true, true),
            tuple(true, true, true, true, true, true, true, true, true, true, true, true, true,
                true, true),
            tuple(true, true, true, true, true, true, true, true, true, true, true, true, true,
                true, true)
        );
  }

  @DisplayName("상세 조건(도 행정구역, 보유시설)으로 오피스를 검색할 수 있다. 검색된 오피스는 페이징 처리가 되어 반환된다.")
  @Test
  public void searchOfficeByDetailConditionTest2() {
    // given
    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));

    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);

    addOfficeData(savedOfficeOwner);

    OfficeDetailSearchCond cond = new OfficeDetailSearchCond();
    cond.setLegion("경상남도");
    cond.setHaveWhiteBoard(true);
    cond.setHavePublicKitchen(true);

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Office> offices = officeQueryService.searchOfficeByDetailCondition(cond, pageRequest);
    List<Office> content = offices.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("name", "leaseFee", "maxCapacity", "officeAddress")
        .containsExactlyInAnyOrder(
            tuple("office1", 500000L, 5, "경상남도 김해시 삼계동 삼계로"),
            tuple("office4", 1500000L, 10, "경상남도 진영시 가츠동 가츠로")
        );

    assertThat(content)
        .extracting(Office::getOfficeLocation)
        .extracting(OfficeLocation::getAddress)
        .extracting("legion", "city", "town", "village", "street", "zipcode")
        .containsExactlyInAnyOrder(
            tuple("경상남도", "김해시", "삼계동", "", "삼계로", 12345),
            tuple("경상남도", "진영시", "가츠동", "", "가츠로", 12598)
        );

    assertThat(content)
        .extracting(Office::getOfficeLocation)
        .extracting("latitude", "longitude")
        .containsExactlyInAnyOrder(
            tuple(127.1315, 37.4562),
            tuple(127.1284, 37.4420)
        );

    assertThat(content)
        .extracting(Office::getOfficeCondition)
        .extracting("airCondition", "heaterCondition", "cafe", "printer", "packageSendService",
            "doorLock", "fax", "publicKitchen", "publicLounge", "privateLocker", "tvProjector",
            "whiteboard", "wifi", "showerBooth", "storage")
        .containsExactlyInAnyOrder(
            tuple(false, false, true, true, true, true, true, true, true, true, true, true, true,
                true, true),
            tuple(true, true, true, true, true, true, true, true, true, true, true, true, true,
                true, true)
        );
  }

  private void addOfficeData(OfficeOwner savedOfficeOwner) {
    OfficeCreateRequestDto request = new OfficeCreateRequestDto();
    setOfficeInfo(request, "office1", 5, 500000, 5);
    setOfficeLocation(request, "경상남도", "김해시", "삼계동", "", "삼계로", 12345, 127.1315, 37.4562);
    setOfficeCondition(request, false, false, true, true, true, true,
        true, true, true, true, true, true, true, true, true);

    officeService.createOfficeInfo(request, savedOfficeOwner.getEmail());

    OfficeCreateRequestDto request2 = new OfficeCreateRequestDto();
    setOfficeInfo(request2, "office2", 10, 1000000, 10);
    setOfficeLocation(request2, "경상남도", "김해시", "삼계동", "", "삼계로", 12348, 127.1324, 37.4500);
    setOfficeCondition(request2, true, true, false, true, true, true,
        true, false, false, true, true, true, true, true, true);

    officeService.createOfficeInfo(request2, savedOfficeOwner.getEmail());

    OfficeCreateRequestDto request3 = new OfficeCreateRequestDto();
    setOfficeInfo(request3, "office3", 10, 1500000, 10);
    setOfficeLocation(request3, "부산광역시", "동구", "좌천동", "", "좌천로", 12398, 127.1524, 37.4320);
    setOfficeCondition(request3, true, true, true, true, true, true,
        true, true, true, true, true, true, true, true, true);

    officeService.createOfficeInfo(request3, savedOfficeOwner.getEmail());

    OfficeCreateRequestDto request4 = new OfficeCreateRequestDto();
    setOfficeInfo(request4, "office4", 10, 1500000, 10);
    setOfficeLocation(request4, "경상남도", "진영시", "가츠동", "", "가츠로", 12598, 127.1284, 37.4420);
    setOfficeCondition(request4, true, true, true, true, true, true,
        true, true, true, true, true, true, true, true, true);

    officeService.createOfficeInfo(request4, savedOfficeOwner.getEmail());

    OfficeCreateRequestDto request5 = new OfficeCreateRequestDto();
    setOfficeInfo(request5, "office5", 15, 2000000, 10);
    setOfficeLocation(request5, "경상남도", "김해시", "내외동", "", "내외로",  12508, 127.1286, 37.4490);
    setOfficeCondition(request5, true, true, true, true, true, true,
        true, false, false, true, true, true, true, false, true);

    officeService.createOfficeInfo(request5, savedOfficeOwner.getEmail());
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
}
