package com.dokkebi.officefinder.service.officeowner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.security.TokenProvider;
import com.dokkebi.officefinder.service.officeowner.dto.OfficeOwnerServiceDto.RentalStatusDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class OfficeOwnerServiceTest {

  @Mock
  private TokenProvider tokenProvider;
  @Mock
  private LeaseRepository leaseRepository;
  @Mock
  private OfficeRepository officeRepository;

  @InjectMocks
  private OfficeOwnerService officeOwnerService;

  private final int PERIOD = 5;

  private List<Lease> leases;
  private final LocalDate today = LocalDate.now();
  private final LocalDate start = today.minusMonths(PERIOD);
  private final LocalDate startDate = LocalDate.of(start.getYear(), start.getMonth(), 1);
  private final List<LeaseStatus> leaseStatus = Arrays.asList(LeaseStatus.EXPIRED,
      LeaseStatus.PROCEEDING);

  @BeforeEach
  void setUpData() {
    OfficeOwner officeOwner = OfficeOwner.builder()
        .name("hong")
        .email("bippr@gmail.com")
        .password("1234")
        .businessNumber("12345")
        .point(0)
        .roles(Set.of("ROLE_OFFICE_OWNER"))
        .build();

    Customer customer = Customer.builder()
        .name("hong")
        .email("bippr@gmail.com")
        .password("encodedPassword")
        .point(0)
        .roles(Set.of("ROLE_CUSTOMER"))
        .build();

    Office office = Office.builder()
        .id(1L)
        .officeAddress("서울")
        .maxCapacity(100)
        .maxRoomCount(10)
        .leaseFee(10000)
        .owner(officeOwner)
        .name("오피스1")
        .build();

    leases = new ArrayList<>();
    Lease lease = Lease.builder()
        .customer(customer)
        .office(office)
        .leaseStartDate(startDate)
        .leaseEndDate(startDate.plusMonths(6))
        .leaseStatus(LeaseStatus.PROCEEDING)
        .price(1000)
        .build();
    Lease lease2 = Lease.builder()
        .customer(customer)
        .office(office)
        .leaseStartDate(startDate.plusMonths(1))
        .leaseEndDate(startDate.plusMonths(6))
        .leaseStatus(LeaseStatus.EXPIRED)
        .price(2000)
        .build();

    leases.add(lease);
    leases.add(lease2);

    given(tokenProvider.getUserIdFromHeader(anyString()))
        .willReturn(1L);

    given(officeRepository.findByIdAndOwnerId(anyLong(), anyLong()))
        .willReturn(Optional.of(office));
  }

  @Test
  void getOfficeRevenue() {
    // given
    given(leaseRepository.findOfficeRevenueLastSixMonth(anyLong(),any(),any(),any()))
        .willReturn(leases);

    // when
    HashMap<String, Long> result = officeOwnerService.getOfficeRevenue(1L, "jwtHeader");

    // then
    assertEquals(result.size(),PERIOD+1);
    assertEquals(2000,result.get(startDate.plusMonths(1).toString().substring(0,7)));
    assertEquals(1000,result.get(startDate.toString().substring(0,7)));
  }

  @Test
  void getOfficeRentalStatus() {
    // given
    given(leaseRepository.countByOfficeIdAndLeaseStatus(anyLong(),any()))
        .willReturn(3);

    // when
    RentalStatusDto result = officeOwnerService.getOfficeRentalStatus(1L, "jwtHeader");

    // given
    assertEquals(10, result.getOfficeRoomCount());
    assertEquals(0.3,result.getLeaseRate());
    assertEquals(3, result.getRoomsInUse());
  }
}