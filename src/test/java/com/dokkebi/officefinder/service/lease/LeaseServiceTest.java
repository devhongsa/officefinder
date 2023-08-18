package com.dokkebi.officefinder.service.lease;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeRequestDto;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeaseServiceTest {

  @Mock
  private LeaseRepository leaseRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private OfficeRepository officeRepository;

  @InjectMocks
  private LeaseService leaseService;

  /*
  @Test
  @DisplayName("공유 오피스 임대 정보 생성 성공")
  public void leaseOffice_Success() {
    // Given
    final String testEmail = "test@example.com";
    final long testOfficeId = 1L;
    final LocalDate testStartDate = LocalDate.of(2023, 01, 03);
    final int testMonths = 5; // 이용 개월 수
    final int testCustomerCount = 4; // 이용 인원
    final boolean testIsMonthlyPay = false;
    final long testCustomerPoint = 500000L; // 고객 포인트
    final long testOfficeLeaseFee = 100000L; // 1개월당 이용 금액
    final int testOfficeMaxCapacity = 4;

    LeaseOfficeRequestDto requestDto = LeaseOfficeRequestDto.builder()
        .email(testEmail)
        .officeId(testOfficeId)
        .startDate(testStartDate)
        .months(testMonths)
        .customerCount(testCustomerCount)
        .isMonthlyPay(testIsMonthlyPay)
        .build();

    Customer testCustomer = Customer.builder()
        .email(testEmail)
        .name("testName")
        .point(testCustomerPoint)
        .build();

    Office testOffice = Office.builder()
        .id(testOfficeId)
        .name("testOffice")
        .leaseFee(testOfficeLeaseFee)
        .maxCapacity(testOfficeMaxCapacity)
        .build();

    given(customerRepository.findByEmail(anyString())).willReturn(Optional.of(testCustomer));
    given(officeRepository.findById(anyLong())).willReturn(Optional.of(testOffice));

    // When
    LeaseOfficeServiceResponse resp = leaseService.leaseOffice(requestDto);

    // Then
    verify(leaseRepository, times(1)).save(any());
    assertThat(resp.getCustomerEmail()).isEqualTo(testEmail);
    assertThat(resp.getOfficeName()).isEqualTo("testOffice");
    assertThat(resp.getPrice()).isEqualTo(testCustomerPoint);
    assertThat(resp.getLeaseStatus()).isEqualTo(LeaseStatus.AWAIT);
    assertThat(resp.getStartDate()).isEqualTo(testStartDate);
    assertThat(resp.getEndDate()).isEqualTo(testStartDate.plusMonths(testMonths));
  }*/

  @Test
  @DisplayName("이메일에 해당하는 유저가 없는 경우")
  public void leaseOffice_FAIL_EmailNotRegistered() {
    // Given
    final String testEmail = "test@example.com";
    final long testOfficeId = 1L;
    final LocalDate testStartDate = LocalDate.of(2023, 01, 03);
    final int testMonths = 5; // 이용 개월 수
    final int testCustomerCount = 4; // 이용 인원
    final boolean testIsMonthlyPay = false;

    LeaseOfficeRequestDto requestDto = LeaseOfficeRequestDto.builder()
        .email(testEmail)
        .officeId(testOfficeId)
        .startDate(testStartDate)
        .months(testMonths)
        .customerCount(testCustomerCount)
        .isMonthlyPay(testIsMonthlyPay)
        .build();

    given(customerRepository.findByEmail(anyString())).willReturn(Optional.empty());

    // When
    Exception exception = assertThrows(CustomException.class, () -> {
      leaseService.leaseOffice(requestDto);
    });

    // Then
    assertTrue(exception instanceof CustomException);
    assertEquals(CustomErrorCode.EMAIL_NOT_REGISTERED, ((CustomException) exception).getErrorCode());
  }

  @Test
  @DisplayName("오피스 ID에 해당하는 오피스가 존재하지 않을 경우 실패")
  public void leaseOffice_Fail_InvalidOfficeId() {
    // Given
    final String testEmail = "test@example.com";
    final long testOfficeId = 1L;
    final LocalDate testStartDate = LocalDate.of(2023, 01, 03);
    final int testMonths = 5; // 이용 개월 수
    final int testCustomerCount = 4; // 이용 인원
    final boolean testIsMonthlyPay = false;
    final long testCustomerPoint = 500000L;

    LeaseOfficeRequestDto requestDto = LeaseOfficeRequestDto.builder()
        .email(testEmail)
        .officeId(testOfficeId)
        .startDate(testStartDate)
        .months(testMonths)
        .customerCount(testCustomerCount)
        .isMonthlyPay(testIsMonthlyPay)
        .build();

    Customer testCustomer = Customer.builder()
        .id(1L)
        .email(testEmail)
        .name("testName")
        .point(testCustomerPoint)
        .build();

    given(customerRepository.findByEmail(anyString())).willReturn(Optional.of(testCustomer));
    given(officeRepository.findById(anyLong())).willReturn(Optional.empty());

    // When
    Exception exception = assertThrows(CustomException.class, () -> {
      leaseService.leaseOffice(requestDto);
    });

    // Then
    assertTrue(exception instanceof CustomException);
    assertEquals(CustomErrorCode.INVALID_OFFICE_ID, ((CustomException) exception).getErrorCode());
  }

  @Test
  @DisplayName("오피스 수용 인원을 초과할때 실패")
  public void leaseOffice_Fail_OfficeOverCapacity() {
    // Given
    final String testEmail = "test@example.com";
    final long testOfficeId = 1L;
    final LocalDate testStartDate = LocalDate.of(2023, 01, 03);
    final int testMonths = 5; // 이용 개월 수
    final int testCustomerCount = 6; // 이용 인원
    final boolean testIsMonthlyPay = false;
    final long testCustomerPoint = 500000L; // 고객 포인트
    final long testOfficeLeaseFee = 100000L; // 1개월당 이용 금액
    final int testOfficeMaxCapacity = 4; // 오피스 최대 수용 인원

    LeaseOfficeRequestDto requestDto = LeaseOfficeRequestDto.builder()
        .email(testEmail)
        .officeId(testOfficeId)
        .startDate(testStartDate)
        .months(testMonths)
        .customerCount(testCustomerCount)
        .isMonthlyPay(testIsMonthlyPay)
        .build();

    Customer testCustomer = Customer.builder()
        .id(1L)
        .email(testEmail)
        .name("testName")
        .point(testCustomerPoint)
        .build();

    Office testOffice = Office.builder()
        .id(testOfficeId)
        .name("testOffice")
        .leaseFee(testOfficeLeaseFee)
        .maxCapacity(testOfficeMaxCapacity)
        .build();

    given(customerRepository.findByEmail(anyString())).willReturn(Optional.of(testCustomer));
    given(officeRepository.findById(anyLong())).willReturn(Optional.of(testOffice));

    // When
    Exception exception = assertThrows(CustomException.class, () -> {
      leaseService.leaseOffice(requestDto);
    });

    // Then
    assertTrue(exception instanceof CustomException);
    assertEquals(CustomErrorCode.OFFICE_OVER_CAPACITY, ((CustomException) exception).getErrorCode());
  }

  @Test
  @DisplayName("고객 포인트가 부족할 때 실패")
  public void leaseOffice_Fail_InsufficientPoints() {
    // Given
    final String testEmail = "test@example.com";
    final long testOfficeId = 1L;
    final LocalDate testStartDate = LocalDate.of(2023, 01, 03);
    final int testMonths = 5; // 이용 개월 수
    final int testCustomerCount = 4; // 이용 인원
    final boolean testIsMonthlyPay = false;
    final long testCustomerPoint = 200000L; // 고객 포인트, 현재 테스트에서 총 금액은 50만원으로 계산됨
    final long testOfficeLeaseFee = 100000L; // 1개월당 이용 금액
    final int testOfficeMaxCapacity = 4;

    LeaseOfficeRequestDto requestDto = LeaseOfficeRequestDto.builder()
        .email(testEmail)
        .officeId(testOfficeId)
        .startDate(testStartDate)
        .months(testMonths)
        .customerCount(testCustomerCount)
        .isMonthlyPay(testIsMonthlyPay)
        .build();

    Customer testCustomer = Customer.builder()
        .id(1L)
        .email(testEmail)
        .name("testName")
        .point(testCustomerPoint)
        .build();

    Office testOffice = Office.builder()
        .id(testOfficeId)
        .name("testOffice")
        .leaseFee(testOfficeLeaseFee)
        .maxCapacity(testOfficeMaxCapacity)
        .build();

    given(customerRepository.findByEmail(anyString())).willReturn(Optional.of(testCustomer));
    given(officeRepository.findById(anyLong())).willReturn(Optional.of(testOffice));

    // When
    Exception exception = assertThrows(CustomException.class, () -> {
      leaseService.leaseOffice(requestDto);
    });

    // Then
    assertTrue(exception instanceof CustomException);
    assertEquals(CustomErrorCode.INSUFFICIENT_POINTS, ((CustomException) exception).getErrorCode());
  }
}