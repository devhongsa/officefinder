package com.dokkebi.officefinder.repository.lease;

import static com.dokkebi.officefinder.entity.QCustomer.customer;
import static com.dokkebi.officefinder.entity.lease.QLease.lease;
import static com.dokkebi.officefinder.entity.office.QOffice.office;

import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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

}