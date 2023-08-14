package com.dokkebi.officefinder.repository.office;

import static com.dokkebi.officefinder.entity.office.QOffice.office;

import com.dokkebi.officefinder.controller.office.dto.OfficeBasicSearchCond;
import com.dokkebi.officefinder.controller.office.dto.OfficeDetailSearchCond;
import com.dokkebi.officefinder.entity.office.Office;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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
  public Page<Office> findByDetailCondition(OfficeDetailSearchCond cond, Pageable pageable) {
    List<Office> result = queryFactory.selectFrom(office)
        .join(office.officeCondition).fetchJoin()
        .join(office.officeLocation).fetchJoin()
        .where(
            legionEquals(cond.getLegion()),
            cityEquals(cond.getCity()),
            townEquals(cond.getTown()),
            villageEquals(cond.getVillage()),
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
            haveStorage(cond.getHaveStorage())
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
            villageEquals(cond.getVillage()),
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
            haveStorage(cond.getHaveStorage())
        );

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<Office> findByBasicCondition(OfficeBasicSearchCond cond, Pageable pageable) {
    List<Office> result = queryFactory.selectFrom(office)
        .join(office.officeCondition).fetchJoin()
        .join(office.officeLocation).fetchJoin()
        .where(
            legionEquals(cond.getLegion()),
            cityEquals(cond.getCity()),
            townEquals(cond.getTown()),
            villageEquals(cond.getVillage()),
            maxCapacityLessThan(cond.getMaxCapacity())
        )
        .offset(pageable.getPageNumber())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(office.count())
        .join(office.officeCondition).fetchJoin()
        .join(office.officeLocation).fetchJoin()
        .where(
            legionEquals(cond.getLegion()),
            cityEquals(cond.getCity()),
            townEquals(cond.getTown()),
            villageEquals(cond.getVillage()),
            maxCapacityLessThan(cond.getMaxCapacity())
        );

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }

  private BooleanExpression legionEquals(String legion) {
    return legion != null ? office.officeLocation.address.legion.eq(legion) : null;
  }

  private BooleanExpression cityEquals(String city) {
    return city != null ? office.officeLocation.address.city.eq(city) : null;
  }

  private BooleanExpression townEquals(String town) {
    return town != null ? office.officeLocation.address.village.eq(town) : null;
  }

  private BooleanExpression villageEquals(String village) {
    return village != null ? office.officeLocation.address.village.eq(village) : null;
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
