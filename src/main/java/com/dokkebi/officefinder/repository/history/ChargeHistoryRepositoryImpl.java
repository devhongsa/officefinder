package com.dokkebi.officefinder.repository.history;

import static com.dokkebi.officefinder.entity.QCustomer.customer;
import static com.dokkebi.officefinder.entity.QPointChargeHistory.pointChargeHistory;

import com.dokkebi.officefinder.entity.PointChargeHistory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class ChargeHistoryRepositoryImpl implements ChargeHistoryRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  public ChargeHistoryRepositoryImpl(EntityManager entityManager) {
    queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<PointChargeHistory> findByCustomerEmail(String customerEmail, Pageable pageable) {
    List<PointChargeHistory> content = queryFactory.selectFrom(pointChargeHistory)
        .join(pointChargeHistory.customer, customer).fetchJoin()
        .where(
            customer.email.eq(customerEmail)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(pointChargeHistory.count())
        .from(pointChargeHistory)
        .join(pointChargeHistory.customer, customer).fetchJoin()
        .where(
            customer.email.eq(customerEmail)
        );

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }
}
