package com.dokkebi.officefinder.repository.bookmark;

import static com.dokkebi.officefinder.entity.bookmark.QBookmark.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;

public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom{

  private final JPAQueryFactory queryFactory;
  private final EntityManager em;

  public BookmarkRepositoryImpl(EntityManager entityManager) {
    em = entityManager;
    queryFactory = new JPAQueryFactory(entityManager);
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
