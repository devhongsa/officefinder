package com.dokkebi.officefinder.repository.office;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.dokkebi.officefinder.controller.office.dto.OfficeSearchCond;
import com.dokkebi.officefinder.controller.office.dto.OfficeOverViewDto;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.OfficeCondition;
import com.dokkebi.officefinder.entity.office.OfficeLocation;
import com.dokkebi.officefinder.entity.type.Address;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.office.condition.OfficeConditionRepository;
import com.dokkebi.officefinder.repository.office.location.OfficeLocationRepository;
import com.dokkebi.officefinder.repository.office.picture.OfficePictureRepository;
import com.dokkebi.officefinder.service.review.dto.ReviewOverviewDto;
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

  @Autowired
  private OfficePictureRepository officePictureRepository;

  private final ReviewOverviewDto reviewOverviewDto = ReviewOverviewDto.builder()
      .reviewCount(42).reviewRate(4.82).build();

  @DisplayName("기본 조건인 도, 시, 군, 구, 수용 인원 수로 오피스를 검색할 수 있다. 지정하지 않으면 모든 오피스를 조회한다.")
  @Test
  public void findByBasicCondition() {
    // given
    addData();

    OfficeSearchCond cond = new OfficeSearchCond();
    PageRequest pageRequest = PageRequest.of(0, 20);

    // when
    Page<Office> result = officeRepository.findBySearchCond(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewOverviewDto,
            "none"))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(3)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office2", "경상남도 진영시", 42, 4.82, 500000L),
            tuple("office3", "경상남도 김해시", 42, 4.82, 500000L)
        );
  }

  @DisplayName("기본 조건인 도 위치로 오피스를 검색할 수 있다.")
  @Test
  public void findByBasicConditionWithLocationInfo() {
    // given
    addData();

    OfficeSearchCond cond = new OfficeSearchCond();
    cond.setLegion("경상남도");

    PageRequest pageRequest = PageRequest.of(0, 20);

    // when
    Page<Office> result = officeRepository.findBySearchCond(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewOverviewDto,
            "none"))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(3)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office2", "경상남도 진영시", 42, 4.82, 500000L),
            tuple("office3", "경상남도 김해시", 42, 4.82, 500000L)
        );
  }

  @DisplayName("기본 조건인 도, 시로 오피스를 검색할 수 있다.")
  @Test
  public void findByBasicConditionWithLocationInfo2() {
    // given
    addData();

    OfficeSearchCond cond = new OfficeSearchCond();
    cond.setLegion("경상남도");
    cond.setCity("김해시");

    PageRequest pageRequest = PageRequest.of(0, 20);

    // when
    Page<Office> result = officeRepository.findBySearchCond(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewOverviewDto,
            "none"))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(2)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office3", "경상남도 김해시", 42, 4.82, 500000L)
        );
  }

  @DisplayName("상세 조건으로 오피스를 검색할 수 있다. 지정하지 않으면 모든 오피스를 조회한다.")
  @Test
  public void findByDetailCondition() {
    // given
    addData();

    OfficeSearchCond cond = new OfficeSearchCond();
    PageRequest pageRequest = PageRequest.of(0, 20);

    // when
    Page<Office> result = officeRepository.findBySearchCond(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewOverviewDto,
            "none"))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(3)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office2", "경상남도 진영시", 42, 4.82, 500000L),
            tuple("office3", "경상남도 김해시", 42, 4.82, 500000L)
        );
  }

  @DisplayName("상세 조건인 샤워 가능 여부로 오피스를 검색할 수 있다.")
  @Test
  public void findByDetailConditionWithConditions() {
    // given
    addData();

    OfficeSearchCond cond = new OfficeSearchCond();
    cond.setHaveShowerBooth(true);

    PageRequest pageRequest = PageRequest.of(0, 20);

    // when
    Page<Office> result = officeRepository.findBySearchCond(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewOverviewDto,
            "none"))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(2)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office2", "경상남도 진영시", 42, 4.82, 500000L)
        );
  }

  @DisplayName("상세 조건인 에어컨 보유 및 히터 보유 및 화이트보드 보유 여부로 오피스를 검색할 수 있다.")
  @Test
  public void findByDetailConditionWithMultipleConditions() {
    // given
    addData();

    OfficeSearchCond cond = new OfficeSearchCond();
    cond.setHaveAirCondition(true);
    cond.setHaveHeater(true);
    cond.setHaveWhiteBoard(true);

    PageRequest pageRequest = PageRequest.of(0, 20);

    // when
    Page<Office> result = officeRepository.findBySearchCond(cond, pageRequest);
    List<OfficeOverViewDto> overViewList = result.getContent().stream()
        .map(content -> OfficeOverViewDto.fromEntity(content, reviewOverviewDto,
            "none"))
        .collect(Collectors.toList());

    // then
    assertThat(overViewList).hasSize(2)
        .extracting("name", "location", "reviewCount", "reviewRate", "leasePrice")
        .containsExactlyInAnyOrder(
            tuple("office", "경상남도 김해시", 42, 4.82, 500000L),
            tuple("office3", "경상남도 김해시", 42, 4.82, 500000L)
        );
  }

  private void addData() {
    OfficeOwner owner = createOfficeOwner("kim", "test@test.com", "1234", "123-456", 1000);
    Office office = createOffice("office", owner, 5, 500000);
    Address address = createAddress("경상남도", "김해시", "삼계동", "", "삼계로",  50898);
    OfficeLocation officeLocation = createOfficeLocation(office, address);
    OfficeCondition officeCondition = createOfficeCondition(office, true, false, true, true, true,
        true,
        false, true, true, true, true, true,
        true, true, true);

    OfficeOwner owner2 = createOfficeOwner("park", "test2@test.com", "1234", "123-457", 1000);
    Office office2 = createOffice("office2", owner2, 5, 500000);
    Address address2 = createAddress("경상남도", "진영시", "가츠동", "", "가츠로", 50878);
    OfficeLocation officeLocation2 = createOfficeLocation(office2, address2);
    OfficeCondition officeCondition2 = createOfficeCondition(office2, false, false, true, true, true,
        true,
        false, true, true, true, true, true,
        true, true, true);

    OfficeOwner owner3 = createOfficeOwner("lee", "test3@test.com", "1234", "123-497", 1000);
    Office office3 = createOffice("office3", owner3, 5, 500000);
    Address address3 = createAddress("경상남도", "김해시", "내외동", "", "내외로", 50878);
    OfficeLocation officeLocation3 = createOfficeLocation(office3, address3);
    OfficeCondition officeCondition3 = createOfficeCondition(office3, true, false, true, true, true,
        true,
        false, true, true, true, true, true,
        true, false, true);

    officeOwnerRepository.saveAll(List.of(owner, owner2, owner3));
    officeRepository.saveAll(List.of(office, office2, office3));
    officeLocationRepository.saveAll(List.of(officeLocation, officeLocation2, officeLocation3));
    officeConditionRepository.saveAll(List.of(officeCondition, officeCondition2, officeCondition3));
  }

  private Address createAddress(String legion, String city, String town, String village,
      String street,int zipcode) {
    return Address.builder()
        .legion(legion)
        .city(city)
        .village(village)
        .town(town)
        .zipcode(zipcode)
        .build();
  }

  private OfficeLocation createOfficeLocation(Office office, Address address) {
    return OfficeLocation.builder()
        .office(office)
        .address(address)
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

  private OfficeCondition createOfficeCondition(Office office, boolean airCondition, boolean cafe,
      boolean heater,
      boolean printer, boolean packageSendService, boolean doorLock, boolean fax,
      boolean publicKitchen, boolean publicLounge, boolean privateLocker, boolean tvProjector,
      boolean whiteboard, boolean wifi, boolean showerBooth, boolean storage) {

    return OfficeCondition.builder()
        .office(office)
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

  private Office createOffice(String name, OfficeOwner owner, int maxCapacity, long leaseFee) {

    return Office.builder()
        .name(name)
        .owner(owner)
        .maxCapacity(maxCapacity)
        .leaseFee(leaseFee)
        .build();
  }
}