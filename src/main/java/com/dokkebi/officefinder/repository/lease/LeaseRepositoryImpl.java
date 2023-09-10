package com.dokkebi.officefinder.repository.lease;

import static com.dokkebi.officefinder.entity.QCustomer.customer;
import static com.dokkebi.officefinder.entity.lease.QLease.lease;
import static com.dokkebi.officefinder.entity.office.QOffice.office;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class LeaseRepositoryImpl implements LeaseRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  public LeaseRepositoryImpl(EntityManager entityManager) {
    queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<Lease> findByCustomerId(Long customerId, Pageable pageable) {
    List<Lease> content = queryFactory.selectFrom(lease)
        .join(lease.customer, customer).fetchJoin()
        .join(lease.office, office).fetchJoin()
        .where(
            customer.id.eq(customerId)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(lease.count())
        .from(lease)
        .join(lease.customer, customer).fetchJoin()
        .join(lease.office, office).fetchJoin()
        .where(
            customer.id.eq(customerId)
        );

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<Lease> findByOfficeIdAndLeaseStatus(Long officeId, LeaseStatus leaseStatus,
      Pageable pageable) {

    List<Lease> content = queryFactory.selectFrom(lease)
        .join(lease.customer, customer).fetchJoin()
        .join(lease.office, office).fetchJoin()
        .where(
            office.id.eq(officeId),
            lease.leaseStatus.eq(leaseStatus)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(lease.count())
        .from(lease)
        .join(lease.customer, customer).fetchJoin()
        .join(lease.office, office).fetchJoin()
        .where(
            office.id.eq(officeId),
            lease.leaseStatus.eq(leaseStatus)
        );

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public List<Lease> findOfficeRevenueLastSixMonth(long officeId, LocalDate startDate,
      LocalDate today, List<LeaseStatus> leaseStatusList) {

    return queryFactory.selectFrom(lease)
        .join(lease.office, office).fetchJoin()
        .where(
            lease.leaseStartDate.between(startDate, today),
            lease.leaseStatus.in(leaseStatusList)
        )
        .orderBy(lease.leaseStartDate.asc())
        .fetch();
  }

  @Override
  public List<Lease> findTotalRevenueLastSixMonth(List<Long> offices, LocalDate startDate,
      LocalDate today, List<LeaseStatus> leaseStatusList) {

    return queryFactory.selectFrom(lease)
        .join(lease.office, office).fetchJoin()
        .where(
            office.id.in(offices),
            lease.leaseStartDate.between(startDate, today),
            lease.leaseStatus.in(leaseStatusList)
        )
        .orderBy(lease.leaseStartDate.asc())
        .fetch();
  }

  @Override
  public Long countOfficeRoomInUse(Long officeId, List<LeaseStatus> leaseStatus, LocalDate startDate,
      LocalDate endDate) {

    return queryFactory.select(lease.count())
        .from(lease)
        .join(lease.office, office)
        .where(
            lease.leaseStartDate.loe(endDate),
            lease.leaseEndDate.goe(startDate),
            lease.leaseStatus.in(leaseStatus)
        )
        .fetchOne();
  }

  @Override
  public Optional<Lease> findByLeaseId(long leaseId) {
    Lease result = queryFactory.selectFrom(lease)
        .join(lease.office, office).fetchJoin()
        .join(lease.customer, customer).fetchJoin()
        .where(
            lease.id.eq(leaseId)
        )
        .fetchOne();

    return Optional.ofNullable(result);
  }

}