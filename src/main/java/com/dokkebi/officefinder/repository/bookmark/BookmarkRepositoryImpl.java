package com.dokkebi.officefinder.repository.bookmark;

import static com.dokkebi.officefinder.entity.QCustomer.*;
import static com.dokkebi.officefinder.entity.bookmark.QBookmark.*;
import static com.dokkebi.officefinder.entity.office.QOffice.*;

import com.dokkebi.officefinder.entity.QCustomer;
import com.dokkebi.officefinder.entity.bookmark.Bookmark;
import com.dokkebi.officefinder.entity.office.QOffice;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom{

  private final JPAQueryFactory queryFactory;
  private final EntityManager em;

  public BookmarkRepositoryImpl(EntityManager entityManager) {
    em = entityManager;
    queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<Bookmark> findByCustomerId(long customerId, Pageable pageable) {
    List<Bookmark> result = queryFactory.selectFrom(bookmark)
        .join(bookmark.customer, customer).fetchJoin()
        .join(bookmark.office, office).fetchJoin()
        .where(
            customer.id.eq(customerId)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(bookmark.count())
        .from(bookmark)
        .join(bookmark.customer, customer).fetchJoin()
        .join(bookmark.office, office).fetchJoin()
        .where(
            customer.id.eq(customerId)
        );

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }

  @Override
  public void deleteAllByCustomerId(Long customerId) {
    long deleteCount = queryFactory.delete(bookmark)
        .where(bookmark.customer.id.eq(customerId))
        .execute();

    em.clear();
    em.flush();
  }
}
