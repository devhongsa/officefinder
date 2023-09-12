package com.dokkebi.officefinder.repository.office;

import static com.dokkebi.officefinder.entity.QOfficeOwner.officeOwner;
import static com.dokkebi.officefinder.entity.office.QOffice.*;
import static com.dokkebi.officefinder.entity.office.QOffice.office;
import static com.dokkebi.officefinder.entity.office.QOfficeCondition.officeCondition;
import static com.dokkebi.officefinder.entity.office.QOfficeLocation.officeLocation;

import com.dokkebi.officefinder.controller.office.dto.OfficeSearchCond;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.office.QOffice;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class OfficeRepositoryImpl implements OfficeRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public OfficeRepositoryImpl(EntityManager entityManager) {
    queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<Office> findBySearchCond(OfficeSearchCond cond, Pageable pageable) {
    List<Office> result = queryFactory.selectFrom(office)
        .join(office.officeCondition).fetchJoin()
        .join(office.officeLocation).fetchJoin()
        .where(
            legionEquals(cond.getLegion()),
            cityEquals(cond.getCity()),
            townEquals(cond.getTown()),
            maxCapacityLessThan(cond.getMaxCapacity()),
            haveAirCondition(cond.getHaveAirCondition()),
            haveCafe(cond.getHaveCafe()),
            havePrinter(cond.getHavePrinter()),
            packageServiceAvailable(cond.getPackageSendServiceAvailable()),
            haveDoorLock(cond.getHaveDoorLock()),
            faxServiceAvailable(cond.getFaxServiceAvailable()),
            havePublicKitchen(cond.getHavePublicKitchen()),
            havePublicLounge(cond.getHavePublicLounge()),
            havePrivateLocker(cond.getHavePrivateLocker()),
            haveTvProjector(cond.getHaveTvProjector()),
            haveWhiteBoard(cond.getHaveWhiteBoard()),
            haveWifiService(cond.getHaveWifi()),
            haveShowerBooth(cond.getHaveShowerBooth()),
            haveStorage(cond.getHaveStorage()),
            haveParkArea(cond.getHaveParkArea())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(office.count())
        .from(office)
        .join(office.officeCondition)
        .join(office.officeLocation)
        .where(
            legionEquals(cond.getLegion()),
            cityEquals(cond.getCity()),
            townEquals(cond.getTown()),
            maxCapacityLessThan(cond.getMaxCapacity()),
            haveAirCondition(cond.getHaveAirCondition()),
            haveCafe(cond.getHaveCafe()),
            havePrinter(cond.getHavePrinter()),
            packageServiceAvailable(cond.getPackageSendServiceAvailable()),
            haveDoorLock(cond.getHaveDoorLock()),
            faxServiceAvailable(cond.getFaxServiceAvailable()),
            havePublicKitchen(cond.getHavePublicKitchen()),
            havePublicLounge(cond.getHavePublicLounge()),
            havePrivateLocker(cond.getHavePrivateLocker()),
            haveTvProjector(cond.getHaveTvProjector()),
            haveWhiteBoard(cond.getHaveWhiteBoard()),
            haveWifiService(cond.getHaveWifi()),
            haveShowerBooth(cond.getHaveShowerBooth()),
            haveStorage(cond.getHaveStorage()),
            haveParkArea(cond.getHaveParkArea())
        );

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<Office> findByOwnerEmail(String ownerEmail, Pageable pageable) {
    List<Office> result = queryFactory.selectFrom(office)
        .join(office.owner, officeOwner).fetchJoin()
        .join(office.officeCondition, officeCondition).fetchJoin()
        .join(office.officeLocation, officeLocation).fetchJoin()
        .where(
            office.owner.email.eq(ownerEmail)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(office.count())
        .from(office)
        .join(office.owner, officeOwner).fetchJoin()
        .join(office.officeCondition, officeCondition).fetchJoin()
        .join(office.officeLocation, officeLocation).fetchJoin()
        .where(
            office.owner.email.eq(ownerEmail)
        );

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }

  @Override
  public Optional<Office> findByOfficeId(Long id) {
    Office result = queryFactory.selectFrom(office)
        .join(office.officeLocation, officeLocation).fetchJoin()
        .join(office.officeCondition, officeCondition).fetchJoin()
        .where(office.id.eq(id))
        .fetchOne();

    return Optional.ofNullable(result);
  }

  private BooleanExpression haveParkArea(Boolean haveParkArea) {
    return haveParkArea != null ? office.officeCondition.parkArea.eq(haveParkArea) : null;
  }

  private BooleanExpression legionEquals(String legion) {
    return legion != null ? office.officeLocation.address.legion.contains(legion) : null;
  }

  private BooleanExpression cityEquals(String city) {
    return city != null ? office.officeLocation.address.city.contains(city) : null;
  }

  private BooleanExpression townEquals(String town) {
    return town != null ? office.officeLocation.address.town.contains(town) : null;
  }

  private BooleanExpression maxCapacityLessThan(Integer maxCapacity) {
    return maxCapacity != null ? office.maxCapacity.loe(maxCapacity) : null;
  }

  private BooleanExpression haveStorage(Boolean haveStorage) {
    return haveStorage != null ? office.officeCondition.storage.eq(haveStorage) : null;
  }

  private BooleanExpression haveShowerBooth(Boolean haveShowerBooth) {
    return haveShowerBooth != null ? office.officeCondition.showerBooth.eq(haveShowerBooth) : null;
  }

  private BooleanExpression haveWifiService(Boolean haveWifi) {
    return haveWifi != null ? office.officeCondition.wifi.eq(haveWifi) : null;
  }

  private BooleanExpression haveWhiteBoard(Boolean haveWhiteBoard) {
    return haveWhiteBoard != null ? office.officeCondition.whiteboard.eq(haveWhiteBoard) : null;
  }

  private BooleanExpression haveTvProjector(Boolean haveTvProjector) {
    return haveTvProjector != null ? office.officeCondition.tvProjector.eq(haveTvProjector) : null;
  }

  private BooleanExpression havePrivateLocker(Boolean havePrivateLocker) {
    return havePrivateLocker != null ? office.officeCondition.privateLocker.eq(havePrivateLocker)
        : null;
  }

  private BooleanExpression havePublicLounge(Boolean havePublicLounge) {
    return havePublicLounge != null ? office.officeCondition.publicLounge.eq(havePublicLounge)
        : null;
  }

  private BooleanExpression havePublicKitchen(Boolean havePublicKitchen) {
    return havePublicKitchen != null ? office.officeCondition.publicKitchen.eq(havePublicKitchen)
        : null;
  }

  private BooleanExpression faxServiceAvailable(Boolean faxServiceAvailable) {
    return faxServiceAvailable != null ? office.officeCondition.fax.eq(faxServiceAvailable) : null;
  }

  private BooleanExpression packageServiceAvailable(Boolean packageSendServiceAvailable) {
    return packageSendServiceAvailable != null ? office.officeCondition.packageSendService.eq(
        packageSendServiceAvailable) : null;
  }

  private BooleanExpression havePrinter(Boolean havePrinter) {
    return havePrinter != null ? office.officeCondition.printer.eq(havePrinter) : null;
  }

  private BooleanExpression haveCafe(Boolean haveCafe) {
    return haveCafe != null ? office.officeCondition.cafe.eq(haveCafe) : null;
  }

  private BooleanExpression haveDoorLock(Boolean haveDoorLock) {
    return haveDoorLock != null ? office.officeCondition.airCondition.eq(haveDoorLock) : null;
  }

  private BooleanExpression haveAirCondition(Boolean haveAirCondition) {
    return haveAirCondition != null ? office.officeCondition.airCondition.eq(haveAirCondition)
        : null;
  }

}
