package com.dokkebi.officefinder.repository.office;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.dokkebi.officefinder.controller.office.dto.OfficeBasicSearchCond;
import com.dokkebi.officefinder.controller.office.dto.OfficeDetailSearchCond;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficeCondition;
import com.dokkebi.officefinder.entity.office.OfficeLocation;
import com.dokkebi.officefinder.entity.type.Address;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.office.condition.OfficeConditionRepository;
import com.dokkebi.officefinder.repository.office.location.OfficeLocationRepository;
import com.dokkebi.officefinder.service.office.dto.OfficeOverViewDto;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class OfficeRepositoryImplTest {

  @Autowired
  private OfficeRepository officeRepository;

  @Autowired
  private OfficeOwnerRepository officeOwnerRepository;

  @Autowired
  private OfficeLocationRepository officeLocationRepository;

  @Autowired
  private OfficeConditionRepository officeConditionRepository;

  @DisplayName("기본 조건인 도, 시, 군, 구, 수용 인원 수로 오피스를 검색할 수 있다. 지정하지 않으면 모든 오피스를 조회한다.")
  @Test
  public void findByBasicCondition() {
    // given
    OfficeOwner owner = createOfficeOwner("kim", "test@test.com", "1234", "123-456", 1000);
    Address address = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-34", 50898, "12-3번지");
    OfficeLocation officeLocation = createOfficeLocation(address, 127.123452, 37.475648);
    OfficeCondition officeCondition = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office = createOffice("office", owner, officeCondition, officeLocation, 5, 500000);

    OfficeOwner owner2 = createOfficeOwner("park", "test2@test.com", "1234", "123-457", 1000);
    Address address2 = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-56", 50878, "12-5번지");
    OfficeLocation officeLocation2 = createOfficeLocation(address2, 127.123452, 37.475648);
    OfficeCondition officeCondition2 = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office2 = createOffice("office2", owner2, officeCondition2, officeLocation2, 5, 500000);

    OfficeOwner owner3 = createOfficeOwner("lee", "test3@test.com", "1234", "123-497", 1000);
    Address address3 = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-57", 50878, "12-9번지");
    OfficeLocation officeLocation3 = createOfficeLocation(address3, 127.123452, 37.475648);
    OfficeCondition officeCondition3 = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office3 = createOffice("office3", owner3, officeCondition3, officeLocation3, 5, 500000);

    officeOwnerRepository.saveAll(List.of(owner, owner2, owner3));
    officeLocationRepository.saveAll(List.of(officeLocation, officeLocation2, officeLocation3));
    officeConditionRepository.saveAll(List.of(officeCondition, officeCondition2, officeCondition3));
    officeRepository.saveAll(List.of(office, office2, office3));

    OfficeBasicSearchCond cond = new OfficeBasicSearchCond();
    PageRequest pageRequest = PageRequest.of(0, 20);

    int reviewCount = 42;
    double reviewRate = 4.82;

    // when
    Page<Office> result = officeRepository.findByBasicCondition(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewCount, reviewRate))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(3)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office2", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office3", "경상남도 김해시", 42, 4.82, 500000L)
        );
  }

  @DisplayName("기본 조건인 도 위치로 오피스를 검색할 수 있다.")
  @Test
  public void findByBasicConditionWithLocationInfo() {
    // given
    OfficeOwner owner = createOfficeOwner("kim", "test@test.com", "1234", "123-456", 1000);
    Address address = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-34", 50898, "12-3번지");
    OfficeLocation officeLocation = createOfficeLocation(address, 127.123452, 37.475648);
    OfficeCondition officeCondition = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office = createOffice("office", owner, officeCondition, officeLocation, 5, 500000);

    OfficeOwner owner2 = createOfficeOwner("park", "test2@test.com", "1234", "123-457", 1000);
    Address address2 = createAddress("경상남도", "진영시", "카츠동", "", "카츠로", "12-56", 50878, "12-5번지");
    OfficeLocation officeLocation2 = createOfficeLocation(address2, 127.123452, 37.475648);
    OfficeCondition officeCondition2 = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office2 = createOffice("office2", owner2, officeCondition2, officeLocation2, 5, 500000);

    OfficeOwner owner3 = createOfficeOwner("lee", "test3@test.com", "1234", "123-497", 1000);
    Address address3 = createAddress("부산광역시", "동구", "좌천동", "", "좌천로", "12-57", 50878, "12-9번지");
    OfficeLocation officeLocation3 = createOfficeLocation(address3, 127.123452, 37.475648);
    OfficeCondition officeCondition3 = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office3 = createOffice("office3", owner3, officeCondition3, officeLocation3, 5, 500000);

    officeOwnerRepository.saveAll(List.of(owner, owner2, owner3));
    officeLocationRepository.saveAll(List.of(officeLocation, officeLocation2, officeLocation3));
    officeConditionRepository.saveAll(List.of(officeCondition, officeCondition2, officeCondition3));
    officeRepository.saveAll(List.of(office, office2, office3));

    OfficeBasicSearchCond cond = new OfficeBasicSearchCond();
    cond.setLegion("경상남도");

    PageRequest pageRequest = PageRequest.of(0, 20);

    int reviewCount = 42;
    double reviewRate = 4.82;

    // when
    Page<Office> result = officeRepository.findByBasicCondition(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewCount, reviewRate))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(2)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office2", "경상남도 진영시", 42, 4.82, 500000L)
        );
  }

  @DisplayName("기본 조건인 도, 시로 오피스를 검색할 수 있다.")
  @Test
  public void findByBasicConditionWithLocationInfo2() {
    // given
    OfficeOwner owner = createOfficeOwner("kim", "test@test.com", "1234", "123-456", 1000);
    Address address = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-34", 50898, "12-3번지");
    OfficeLocation officeLocation = createOfficeLocation(address, 127.123452, 37.475648);
    OfficeCondition officeCondition = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office = createOffice("office", owner, officeCondition, officeLocation, 5, 500000);

    OfficeOwner owner2 = createOfficeOwner("park", "test2@test.com", "1234", "123-457", 1000);
    Address address2 = createAddress("경상남도", "진영시", "카츠동", "", "카츠로", "12-56", 50878, "12-5번지");
    OfficeLocation officeLocation2 = createOfficeLocation(address2, 127.123452, 37.475648);
    OfficeCondition officeCondition2 = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office2 = createOffice("office2", owner2, officeCondition2, officeLocation2, 5, 500000);

    OfficeOwner owner3 = createOfficeOwner("lee", "test3@test.com", "1234", "123-497", 1000);
    Address address3 = createAddress("부산광역시", "동구", "좌천동", "", "좌천로", "12-57", 50878, "12-9번지");
    OfficeLocation officeLocation3 = createOfficeLocation(address3, 127.123452, 37.475648);
    OfficeCondition officeCondition3 = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office3 = createOffice("office3", owner3, officeCondition3, officeLocation3, 5, 500000);

    officeOwnerRepository.saveAll(List.of(owner, owner2, owner3));
    officeLocationRepository.saveAll(List.of(officeLocation, officeLocation2, officeLocation3));
    officeConditionRepository.saveAll(List.of(officeCondition, officeCondition2, officeCondition3));
    officeRepository.saveAll(List.of(office, office2, office3));

    OfficeBasicSearchCond cond = new OfficeBasicSearchCond();
    cond.setLegion("경상남도");
    cond.setCity("김해시");

    PageRequest pageRequest = PageRequest.of(0, 20);

    int reviewCount = 42;
    double reviewRate = 4.82;

    // when
    Page<Office> result = officeRepository.findByBasicCondition(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewCount, reviewRate))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(1)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L)
        );
  }

  @DisplayName("상세 조건으로 오피스를 검색할 수 있다. 지정하지 않으면 모든 오피스를 조회한다.")
  @Test
  public void findByDetailCondition() {
    // given
    OfficeOwner owner = createOfficeOwner("kim", "test@test.com", "1234", "123-456", 1000);
    Address address = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-34", 50898, "12-3번지");
    OfficeLocation officeLocation = createOfficeLocation(address, 127.123452, 37.475648);
    OfficeCondition officeCondition = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office = createOffice("office", owner, officeCondition, officeLocation, 5, 500000);

    OfficeOwner owner2 = createOfficeOwner("park", "test2@test.com", "1234", "123-457", 1000);
    Address address2 = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-56", 50878, "12-5번지");
    OfficeLocation officeLocation2 = createOfficeLocation(address2, 127.123452, 37.475648);
    OfficeCondition officeCondition2 = createOfficeCondition(true, false, true, true, true, true,
        false, true, false, true, true, true,
        true, true, true);

    Office office2 = createOffice("office2", owner2, officeCondition2, officeLocation2, 5, 500000);

    OfficeOwner owner3 = createOfficeOwner("lee", "test3@test.com", "1234", "123-497", 1000);
    Address address3 = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-57", 50878, "12-9번지");
    OfficeLocation officeLocation3 = createOfficeLocation(address3, 127.123452, 37.475648);
    OfficeCondition officeCondition3 = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        false, true, true);

    Office office3 = createOffice("office3", owner3, officeCondition3, officeLocation3, 5, 500000);

    officeOwnerRepository.saveAll(List.of(owner, owner2, owner3));
    officeLocationRepository.saveAll(List.of(officeLocation, officeLocation2, officeLocation3));
    officeConditionRepository.saveAll(List.of(officeCondition, officeCondition2, officeCondition3));
    officeRepository.saveAll(List.of(office, office2, office3));

    OfficeDetailSearchCond cond = new OfficeDetailSearchCond();
    PageRequest pageRequest = PageRequest.of(0, 20);

    int reviewCount = 42;
    double reviewRate = 4.82;

    // when
    Page<Office> result = officeRepository.findByDetailCondition(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewCount, reviewRate))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(3)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office2", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office3", "경상남도 김해시", 42, 4.82, 500000L)
        );
  }

  @DisplayName("상세 조건인 샤워 가능 여부로 오피스를 검색할 수 있다.")
  @Test
  public void findByDetailConditionWithConditions() {
    // given
    OfficeOwner owner = createOfficeOwner("kim", "test@test.com", "1234", "123-456", 1000);
    Address address = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-34", 50898, "12-3번지");
    OfficeLocation officeLocation = createOfficeLocation(address, 127.123452, 37.475648);
    OfficeCondition officeCondition = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office = createOffice("office", owner, officeCondition, officeLocation, 5, 500000);

    OfficeOwner owner2 = createOfficeOwner("park", "test2@test.com", "1234", "123-457", 1000);
    Address address2 = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-56", 50878, "12-5번지");
    OfficeLocation officeLocation2 = createOfficeLocation(address2, 127.123452, 37.475648);
    OfficeCondition officeCondition2 = createOfficeCondition(true, false, true, true, true, true,
        false, true, false, true, true, true,
        true, true, true);

    Office office2 = createOffice("office2", owner2, officeCondition2, officeLocation2, 5, 500000);

    OfficeOwner owner3 = createOfficeOwner("lee", "test3@test.com", "1234", "123-497", 1000);
    Address address3 = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-57", 50878, "12-9번지");
    OfficeLocation officeLocation3 = createOfficeLocation(address3, 127.123452, 37.475648);
    OfficeCondition officeCondition3 = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        false, false, true);

    Office office3 = createOffice("office3", owner3, officeCondition3, officeLocation3, 5, 500000);

    officeOwnerRepository.saveAll(List.of(owner, owner2, owner3));
    officeLocationRepository.saveAll(List.of(officeLocation, officeLocation2, officeLocation3));
    officeConditionRepository.saveAll(List.of(officeCondition, officeCondition2, officeCondition3));
    officeRepository.saveAll(List.of(office, office2, office3));

    OfficeDetailSearchCond cond = new OfficeDetailSearchCond();
    cond.setHaveShowerBooth(true);

    PageRequest pageRequest = PageRequest.of(0, 20);

    int reviewCount = 42;
    double reviewRate = 4.82;

    // when
    Page<Office> result = officeRepository.findByDetailCondition(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewCount, reviewRate))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(2)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office2", "경상남도 김해시", 42, 4.82, 500000L)
        );
  }

  @DisplayName("상세 조건인 에어컨 보유 및 히터 보유 및 화이트보드 보유 여부로 오피스를 검색할 수 있다.")
  @Test
  public void findByDetailConditionWithMultipleConditions() {
    // given
    OfficeOwner owner = createOfficeOwner("kim", "test@test.com", "1234", "123-456", 1000);
    Address address = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-34", 50898, "12-3번지");
    OfficeLocation officeLocation = createOfficeLocation(address, 127.123452, 37.475648);
    OfficeCondition officeCondition = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        true, true, true);

    Office office = createOffice("office", owner, officeCondition, officeLocation, 5, 500000);

    OfficeOwner owner2 = createOfficeOwner("park", "test2@test.com", "1234", "123-457", 1000);
    Address address2 = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-56", 50878, "12-5번지");
    OfficeLocation officeLocation2 = createOfficeLocation(address2, 127.123452, 37.475648);
    OfficeCondition officeCondition2 = createOfficeCondition(true, false, true, true, true, true,
        false, true, false, true, true, false,
        true, true, true);

    Office office2 = createOffice("office2", owner2, officeCondition2, officeLocation2, 5, 500000);

    OfficeOwner owner3 = createOfficeOwner("lee", "test3@test.com", "1234", "123-497", 1000);
    Address address3 = createAddress("경상남도", "김해시", "삼계동", "", "삼계로", "12-57", 50878, "12-9번지");
    OfficeLocation officeLocation3 = createOfficeLocation(address3, 127.123452, 37.475648);
    OfficeCondition officeCondition3 = createOfficeCondition(true, false, true, true, true, true,
        false, true, true, true, true, true,
        false, true, true);

    Office office3 = createOffice("office3", owner3, officeCondition3, officeLocation3, 5, 500000);

    officeOwnerRepository.saveAll(List.of(owner, owner2, owner3));
    officeLocationRepository.saveAll(List.of(officeLocation, officeLocation2, officeLocation3));
    officeConditionRepository.saveAll(List.of(officeCondition, officeCondition2, officeCondition3));
    officeRepository.saveAll(List.of(office, office2, office3));

    OfficeDetailSearchCond cond = new OfficeDetailSearchCond();
    cond.setHaveAirCondition(true);
    cond.setHaveHeater(true);
    cond.setHaveWhiteBoard(true);

    PageRequest pageRequest = PageRequest.of(0, 20);

    int reviewCount = 42;
    double reviewRate = 4.82;

    // when
    Page<Office> result = officeRepository.findByDetailCondition(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewCount, reviewRate))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(2)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office3", "경상남도 김해시", 42, 4.82, 500000L)
        );
  }

  private Address createAddress(String legion, String city, String town, String village,
      String street, String buildingNumber, int zipcode, String bungi) {
    return Address.builder()
        .legion(legion)
        .city(city)
        .village(village)
        .street(street)
        .town(town)
        .buildingNumber(buildingNumber)
        .zipcode(zipcode)
        .bungi(bungi)
        .build();
  }

  private OfficeLocation createOfficeLocation(Address address, double longitude, double latitude) {
    return OfficeLocation.builder()
        .address(address)
        .longitude(longitude)
        .latitude(latitude)
        .build();
  }

  private OfficeOwner createOfficeOwner(String name, String email, String password,
      String businessNumber, long point) {

    return OfficeOwner.builder()
        .name(name)
        .email(email)
        .password(password)
        .businessNumber(businessNumber)
        .point(point)
        .build();
  }

  private OfficeCondition createOfficeCondition(boolean airCondition, boolean cafe, boolean heater,
      boolean printer, boolean packageSendService, boolean doorLock, boolean fax,
      boolean publicKitchen, boolean publicLounge, boolean privateLocker, boolean tvProjector,
      boolean whiteboard, boolean wifi, boolean showerBooth, boolean storage) {

    return OfficeCondition.builder()
        .airCondition(airCondition)
        .cafe(cafe)
        .packageSendService(packageSendService)
        .heaterCondition(heater)
        .printer(printer)
        .doorLock(doorLock)
        .publicKitchen(publicKitchen)
        .publicLounge(publicLounge)
        .showerBooth(showerBooth)
        .fax(fax)
        .tvProjector(tvProjector)
        .whiteboard(whiteboard)
        .wifi(wifi)
        .storage(storage)
        .privateLocker(privateLocker)
        .build();
  }

  private Office createOffice(String name, OfficeOwner owner, OfficeCondition officeCondition,
      OfficeLocation officeLocation, int maxCapacity, long leaseFee) {

    return Office.builder()
        .name(name)
        .owner(owner)
        .officeCondition(officeCondition)
        .officeLocation(officeLocation)
        .maxCapacity(maxCapacity)
        .leaseFee(leaseFee)
        .build();
  }
}