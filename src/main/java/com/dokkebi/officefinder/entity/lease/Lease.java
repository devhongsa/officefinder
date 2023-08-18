package com.dokkebi.officefinder.entity.lease;

import com.dokkebi.officefinder.entity.BaseEntity;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeRequestDto;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Lease extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "lease_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "office_id")
  private Office office;

  @Column(name = "lease_price")
  private long price;

  @Enumerated(EnumType.STRING)
  private LeaseStatus leaseStatus;

  private LocalDate leaseStartDate;
  private LocalDate leaseEndDate;

  private Boolean isMonthlyPay;

  @Builder
  private Lease(Long id, Customer customer, Office office, long price, LeaseStatus leaseStatus,
      LocalDate leaseStartDate, LocalDate leaseEndDate, Boolean isMonthlyPay) {
    this.id = id;
    this.customer = customer;
    this.office = office;
    this.price = price;
    this.leaseStatus = leaseStatus;
    this.leaseStartDate = leaseStartDate;
    this.leaseEndDate = leaseEndDate;
    this.isMonthlyPay = isMonthlyPay;
  }

  public static Lease fromRequest(Customer customer, Office office, long totalPrice,
      LeaseOfficeRequestDto request) {

    return Lease.builder()
        .customer(customer)
        .office(office)
        .price(totalPrice)
        .leaseStatus(LeaseStatus.AWAIT)
        .leaseStartDate(request.getStartDate())
        .leaseEndDate(request.getStartDate().plusMonths(request.getMonths()))
        .isMonthlyPay(request.isMonthlyPay())
        .build();
  }

  /*
  임대 계약 상태 변경 메서드
   */
  public void changeLeaseStatus(LeaseStatus newLeaseStatus) {
    this.leaseStatus = newLeaseStatus;
  }
}