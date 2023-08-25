package com.dokkebi.officefinder.config.batch;

import static org.junit.jupiter.api.Assertions.*;

import com.dokkebi.officefinder.controller.office.dto.OfficeAddress;
import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeOption;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.repository.office.condition.OfficeConditionRepository;
import com.dokkebi.officefinder.repository.office.location.OfficeLocationRepository;
import com.dokkebi.officefinder.repository.office.picture.OfficePictureRepository;
import com.dokkebi.officefinder.service.office.OfficeService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(BatchTestConfig.class)
class BatchJobConfigTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private LeaseRepository leaseRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private OfficeRepository officeRepository;

  @Autowired
  private OfficeService officeService;

  @Autowired
  private OfficeOwnerRepository officeOwnerRepository;
  @Autowired
  private OfficePictureRepository officePictureRepository;
  @Autowired
  private OfficeConditionRepository officeConditionRepository;
  @Autowired
  private OfficeLocationRepository officeLocationRepository;

  @AfterEach
  public void tearDown(){
    // Lease가 Customer와 Office를 참조하고 있으므로 Lease를 먼저 삭제
    leaseRepository.deleteAllInBatch();

    // OfficePicture, OfficeCondition, OfficeLocation은 모두 Office를 참조하고 있으므로, 이들을 먼저 삭제
    officePictureRepository.deleteAllInBatch();
    officeConditionRepository.deleteAllInBatch();
    officeLocationRepository.deleteAllInBatch();

    // OfficeOwner 참고 하므로 먼저 삭제
    officeRepository.deleteAllInBatch();

    customerRepository.deleteAllInBatch();
    officeOwnerRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("이용 기간이 만료된 임대의 상태를 Expired 상태로 바꾸는 배치 기능 테스트")
  public void testUpdateExpiredLeaseJob() throws Exception{

    Customer customer = createCustomer("customer1", "test@test.com", "1234",
        Set.of("ROLE_CUSTOMER"), 1000000);

    Customer customer2 = createCustomer("customer2", "test2@test.com", "1234",
        Set.of("ROLE_CUSTOMER"), 1000000);

    Customer savedCustomer = customerRepository.save(customer);
    Customer savedCustomer2 = customerRepository.save(customer2);

    OfficeOwner officeOwner = createOfficeOwner("kim", "owner@test.com", "12345", "123-45", 1000L,
        Set.of("ROLE_OFFICE_OWNER"));
    OfficeOwner savedOfficeOwner = officeOwnerRepository.save(officeOwner);

    OfficeCreateRequestDto request = new OfficeCreateRequestDto();
    setOfficeInfo(request, "office1", 5, 500000, 5);
    request.setAddress(setOfficeLocation("경상남도", "김해시", "삼계동", "", "경상남도 김해시 삼계동 삼계로 223", 12345));
    request.setOfficeOption(setOfficeCondition(false, false, true, true, true, true,
        true, true, true, true, true, true, true, true, true));

    Long savedId = officeService.createOfficeInfo(request, new ArrayList<>(),
        savedOfficeOwner.getEmail());

    Office office = officeRepository.findById(savedId).get();

    Lease lease = createLease(savedCustomer, office, 100000L, LeaseStatus.PROCEEDING, LocalDate.now().minusMonths(2), LocalDate.now().minusDays(1));
    Lease lease2 = createLease(savedCustomer2, office, 100000L, LeaseStatus.PROCEEDING, LocalDate.now().minusMonths(2), LocalDate.now().minusDays(1));

    leaseRepository.save(lease);
    leaseRepository.save(lease2);

    // 배치 job 실행
    JobParameters jobParameters = new JobParametersBuilder()
        .addDate("expireDate", java.sql.Date.valueOf(LocalDate.now().minusDays(1)))
        .toJobParameters();

    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    // Lease 객체들의 상태 확인
    List<Lease> updatedLeases = leaseRepository.findAll();

    for(Lease l : updatedLeases){
      assertEquals(LeaseStatus.EXPIRED, l.getLeaseStatus());
    }
  }

  private Customer createCustomer(String name, String email, String password, Set<String> roles,
      int point) {
    return Customer.builder()
        .name(name)
        .email(email)
        .password(password)
        .roles(roles)
        .point(point)
        .build();
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

  private OfficeAddress setOfficeLocation(String legion, String city, String town, String village,
      String street, int zipcode) {

    return OfficeAddress.builder()
        .legion(legion)
        .city(city)
        .town(town)
        .village(village)
        .street(street)
        .zipcode(String.valueOf(zipcode))
        .build();
  }

  private OfficeOption setOfficeCondition(boolean airCondition, boolean heaterCondition,
      boolean cafe,
      boolean printer, boolean packageSendService, boolean doorLock, boolean fax,
      boolean publicKitchen, boolean publicLounge, boolean privateLocker, boolean tvProjector,
      boolean whiteboard, boolean wifi, boolean showerBooth, boolean storage) {

    return OfficeOption.builder()
        .haveAirCondition(airCondition)
        .haveHeater(heaterCondition)
        .haveCafe(cafe)
        .havePrinter(printer)
        .packageSendServiceAvailable(packageSendService)
        .haveDoorLock(doorLock)
        .faxServiceAvailable(fax)
        .havePublicKitchen(publicKitchen)
        .havePublicLounge(publicLounge)
        .havePrivateLocker(privateLocker)
        .haveTvProjector(tvProjector)
        .haveWhiteBoard(whiteboard)
        .haveWifi(wifi)
        .haveShowerBooth(showerBooth)
        .haveStorage(storage)
        .build();
  }

  private Lease createLease(Customer customer, Office office, long price, LeaseStatus leaseStatus, LocalDate startDate, LocalDate endDate) {
    return Lease.builder()
        .customer(customer)
        .office(office)
        .price(price)
        .leaseStatus(leaseStatus)
        .leaseStartDate(startDate)
        .leaseEndDate(endDate)
        .build();
  }
}