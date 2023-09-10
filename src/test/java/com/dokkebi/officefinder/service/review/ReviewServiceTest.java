package com.dokkebi.officefinder.service.review;

import static com.dokkebi.officefinder.exception.CustomErrorCode.LEASE_NOT_EXPIRED;
import static com.dokkebi.officefinder.exception.CustomErrorCode.LEASE_NOT_FOUND;
import static com.dokkebi.officefinder.exception.CustomErrorCode.LEASE_OWNER_NOT_MATCH;
import static com.dokkebi.officefinder.exception.CustomErrorCode.REVIEW_ALREADY_EXISTS;
import static com.dokkebi.officefinder.exception.CustomErrorCode.REVIEW_NOT_EXISTS;
import static com.dokkebi.officefinder.exception.CustomErrorCode.REVIEW_OWNER_NOT_MATCH;
import static com.dokkebi.officefinder.exception.CustomErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dokkebi.officefinder.controller.auth.dto.Auth;
import com.dokkebi.officefinder.controller.auth.dto.Auth.SignUpCustomer;
import com.dokkebi.officefinder.controller.auth.dto.Auth.SignUpOfficeOwner;
import com.dokkebi.officefinder.controller.auth.dto.Auth.SignUpResponseCustomer;
import com.dokkebi.officefinder.controller.auth.dto.Auth.SignUpResponseOfficeOwner;
import com.dokkebi.officefinder.controller.office.dto.OfficeAddress;
import com.dokkebi.officefinder.controller.office.dto.OfficeCreateRequestDto;
import com.dokkebi.officefinder.controller.office.dto.OfficeOption;
import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.SubmitControllerRequest;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.repository.office.condition.OfficeConditionRepository;
import com.dokkebi.officefinder.repository.office.location.OfficeLocationRepository;
import com.dokkebi.officefinder.repository.office.picture.OfficePictureRepository;
import com.dokkebi.officefinder.service.auth.AuthService;
import com.dokkebi.officefinder.service.lease.LeaseService;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeRequestDto;
import com.dokkebi.officefinder.service.lease.dto.LeaseServiceDto.LeaseOfficeServiceResponse;
import com.dokkebi.officefinder.service.office.OfficeService;
import com.dokkebi.officefinder.service.review.dto.ReviewOverviewDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
public class ReviewServiceTest {

  @Autowired
  private ReviewService reviewService;
  @Autowired
  private LeaseRepository leaseRepository;
  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private OfficeRepository officeRepository;

  @Autowired
  private OfficeLocationRepository officeLocationRepository;
  @Autowired
  private OfficeConditionRepository officeConditionRepository;
  @Autowired
  private OfficePictureRepository officePictureRepository;

  @Autowired
  private AuthService authService;

  @Autowired
  private OfficeService officeService;

  @Autowired
  private LeaseService leaseService;

  @AfterEach
  void tearDown() {
    reviewRepository.deleteAllInBatch();
    leaseRepository.deleteAllInBatch();
    customerRepository.deleteAllInBatch();
    officeConditionRepository.deleteAllInBatch();
    officeLocationRepository.deleteAllInBatch();
    officePictureRepository.deleteAllInBatch();
    officeRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("Submit시 leaseId가 DB에 없는 경우 exception을 리턴한다.")
  public void ReviewSubmit1() throws Exception {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();

    //then
    assertThatThrownBy(
        () -> reviewService.submit(submitControllerRequest, infos.customer.getId(),
            infos.lease.getId() + 1)
    )
        .isInstanceOf(CustomException.class)
        .extracting("errorCode", "errorMessage", "status")
        .contains(
            LEASE_NOT_FOUND, "해당 임대 정보가 조회되지 않습니다.", HttpStatus.BAD_REQUEST
        );
  }

  @Test
  @DisplayName("Submit시 lease 내부의 회원 Id와 submit의 회원 Id가 다른 경우 exception return")
  public void ReviewSubmit2() throws Exception {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();

    //then
    assertThatThrownBy(
        () -> reviewService.submit(submitControllerRequest, infos.customer.getId() + 1,
            infos.lease.getId())
    )
        .isInstanceOf(CustomException.class)
        .extracting("errorCode", "errorMessage", "status")
        .contains(
            LEASE_OWNER_NOT_MATCH, "임대자 본인이 아닙니다.", HttpStatus.BAD_REQUEST
        );
  }

  @Test
  @DisplayName("Submit시 이미 동일한 임대에 대한 리뷰가 존재하는 경우 exception return")
  public void ReviewSubmit3() throws Exception {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();

    reviewRepository.save(Review.builder().lease(infos.lease).build());

    //then
    assertThatThrownBy(() -> reviewService.submit(submitControllerRequest, infos.customer.getId(),
        infos.lease.getId())
    )
        .isInstanceOf(CustomException.class)
        .extracting("errorCode", "errorMessage", "status")
        .contains(
            REVIEW_ALREADY_EXISTS, "이미 리뷰가 있습니다.", HttpStatus.BAD_REQUEST
        );
  }

  @Test
  @DisplayName("Submit시 lease 상태가 EXPIRED가 아닌 경우 exception return")
  public void ReviewSubmit4() throws Exception {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.AWAIT);
    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();

    //then
    assertThatThrownBy(() -> reviewService.submit(submitControllerRequest, infos.customer.getId(),
        infos.lease.getId())
    )
        .isInstanceOf(CustomException.class)
        .extracting("errorCode", "errorMessage", "status")
        .contains(
            LEASE_NOT_EXPIRED, "계약이 만료되지 않았습니다.", HttpStatus.BAD_REQUEST
        );
  }

  @Test
  @DisplayName("Submit 정상 작동하는 경우")
  public void ReviewSubmit5() throws Exception {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();

    // when
    Review savedReview = reviewService.submit(submitControllerRequest, infos.customer.getId(),
        infos.lease.getId());

    //then
    assertThat(savedReview.getCustomerId()).isEqualTo(infos.customer.getId());
    assertThat(infos.office.getReviewCount()).isEqualTo(1L);
    assertThat(infos.office.getTotalRate()).isEqualTo(5L);
  }

  @Test
  @DisplayName("Update시 reviewId가 존재하지 않으면 exception 리턴")
  public void ReviewUpdate1() throws Exception {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    Review review = Review.builder()
        .lease(infos.lease)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(5)
        .description("test").build();
    Review savedReview = reviewRepository.save(review);

    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();

    //then
    assertThatThrownBy(() -> reviewService.update(submitControllerRequest, infos.customer.getId(),
        savedReview.getId() + 1))
        .isInstanceOf(CustomException.class)
        .extracting("errorCode", "errorMessage", "status")
        .contains(
            REVIEW_NOT_EXISTS, "리뷰가 존재하지 않습니다.", HttpStatus.BAD_REQUEST
        );
  }

  @Test
  @DisplayName("Update시 요청하는 회원과 리뷰 작성자가 다를 경우 exception 리턴")
  public void ReviewUpdate2() throws Exception {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    Review review = Review.builder()
        .lease(infos.lease)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(5)
        .description("test").build();
    Review savedReview = reviewRepository.save(review);

    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();

    //then
    assertThatThrownBy(
        () -> reviewService.update(submitControllerRequest, infos.customer.getId() + 1,
            savedReview.getId())
    )
        .isInstanceOf(CustomException.class)
        .extracting("errorCode", "errorMessage", "status")
        .contains(
            REVIEW_OWNER_NOT_MATCH, "리뷰 작성자 본인이 아닙니다.", HttpStatus.BAD_REQUEST
        );
  }

  @Test
  @DisplayName("Update시 정상 작동")
  public void ReviewUpdate3() throws Exception {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    Review review = Review.builder()
        .lease(infos.lease)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(5)
        .description("test").build();
    Review savedReview = reviewRepository.save(review);

    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(1)
        .description("수정 후").build();
    //when
    Review updatedReview = reviewService.update(submitControllerRequest,
        infos.customer.getId(), savedReview.getId());
    //then
    assertThat(updatedReview.getId()).isEqualTo(savedReview.getId());
    assertThat(updatedReview.getRate()).isEqualTo(1);
    assertThat(updatedReview.getDescription()).isEqualTo("수정 후");
  }

  @Test
  @DisplayName("read할때 jwtToken에서 가져온 아이디가 잘못된 경우")
  public void getReviews1() {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);

    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
    Review review1 = Review.builder()
        .lease(infos.lease)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(5)
        .description("test").build();

    reviewRepository.save(review1);

    //then
    assertThatThrownBy(
        () -> reviewService.getReviewsByCustomerId(infos.customer.getId() + 1, pageable))
        .isInstanceOf(CustomException.class)
        .extracting("errorCode", "errorMessage", "status")
        .contains(
            USER_NOT_FOUND, "유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST
        );
  }

  @Test
  @DisplayName("read할때 임대계약은 있지만 리뷰는 없는 경우")
  public void getReviews2() {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);

    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

    //then
    assertThatThrownBy(() -> reviewService.getReviewsByOfficeId(infos.office.getId(), pageable))
        .isInstanceOf(CustomException.class)
        .extracting("errorCode", "errorMessage", "status")
        .contains(
            REVIEW_NOT_EXISTS, "리뷰가 존재하지 않습니다.", HttpStatus.BAD_REQUEST
        );
  }

  @Test
  @DisplayName("리뷰 CustomerEmail로 가져오기 정상 작동")
  public void getReviews3() {

    //given
    Customer customer = customerRepository.save(
        Customer.builder()
            .name("1")
            .email("test@naver.com")
            .password("1")
            .roles(Set.of("customer"))
            .point(0).build()
    );

    Office office = officeRepository.save(
        Office.builder().name("1").build()
    );

    Lease lease1 = leaseRepository.save(
        Lease.builder()
            .customer(customer)
            .office(office)
            .leaseStatus(LeaseStatus.EXPIRED).build()
    );

    Lease lease2 = leaseRepository.save(
        Lease.builder()
            .customer(customer)
            .office(office)
            .leaseStatus(LeaseStatus.EXPIRED).build()
    );

    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
    Review review1 = Review.builder()
        .lease(lease1)
        .customerId(customer.getId())
        .officeId(office.getId())
        .rate(5)
        .description("test").build();

    Review review2 = Review.builder()
        .lease(lease2)
        .customerId(customer.getId())
        .officeId(office.getId())
        .rate(5)
        .description("test").build();

    reviewRepository.save(review1);
    reviewRepository.save(review2);

    //when
    Page<Review> reviews = reviewService.getReviewsByCustomerId(customer.getId(), pageable);

    //then
    List<Review> reviewList = reviews.getContent();
    assertThat(reviewList.get(0).getLease().getCustomer().getName()).isEqualTo("1");
    assertThat(reviewList.get(1).getLease().getCustomer().getName()).isEqualTo("1");
  }

  @Test
  @DisplayName("delete할때 jwtToken의 회원 아이디가 잘못된 경우")
  public void delete1() {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    Review review = Review.builder()
        .lease(infos.lease)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(5)
        .description("test").build();
    Review savedReview = reviewRepository.save(review);

    //then
    assertThatThrownBy(
        () -> reviewService.delete(infos.customer.getId() + 1, savedReview.getId())
    ).isInstanceOf(CustomException.class)
        .extracting("errorCode", "errorMessage", "status")
        .contains(
            USER_NOT_FOUND, "유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST
        );
  }

  @Test
  @DisplayName("delete할때 review가 존재하지 않는 경우")
  public void delete2() {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);

    //then
    assertThatThrownBy(() -> reviewService.delete(infos.customer.getId(), 1L))
        .isInstanceOf(CustomException.class)
        .extracting("errorCode", "errorMessage", "status")
        .contains(
            REVIEW_NOT_EXISTS, "리뷰가 존재하지 않습니다.", HttpStatus.BAD_REQUEST
        );
  }

  @Test
  @DisplayName("delete할때 리뷰 작성자와 현재 회원이 다른 경우")
  public void delete3() {
    //given
    Infos infos1 = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    Review review1 = Review.builder()
        .lease(infos1.lease)
        .customerId(infos1.customer.getId())
        .officeId(infos1.office.getId())
        .rate(5)
        .description("test").build();

    Infos infos2 = makeInfos("2", "test2@naver.com", "2", "customer", 0, LeaseStatus.EXPIRED);

    Review review2 = Review.builder()
        .lease(infos2.lease)
        .customerId(infos2.customer.getId())
        .officeId(infos2.office.getId())
        .rate(5)
        .description("test").build();

    Review savedReview1 = reviewRepository.save(review1);
    Review savedReview2 = reviewRepository.save(review2);

    //when
    assertThatThrownBy(() -> reviewService.delete(infos2.customer.getId(), review1.getId()))
        .isInstanceOf(CustomException.class)
        .extracting("errorCode", "errorMessage", "status")
        .contains(
            REVIEW_OWNER_NOT_MATCH, "리뷰 작성자 본인이 아닙니다.", HttpStatus.BAD_REQUEST
        );
  }

  @Test
  @DisplayName("delete 정상 작동")
  public void delete4() {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    Review review = Review.builder()
        .lease(infos.lease)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(5)
        .description("test").build();
    Review savedReview = reviewRepository.save(review);
    //when
    reviewService.delete(infos.customer.getId(), savedReview.getId());
    //then
    assertThat(reviewRepository.findById(savedReview.getId())).isEmpty();
  }

  @Test
  @DisplayName("officeId로 리뷰 작성일자 역순으로 가져오기 성공")
  public void getByOfficeId() throws InterruptedException {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    Lease lease2 = leaseRepository.save(
        Lease.builder()
            .customer(infos.customer)
            .office(infos.office)
            .leaseStatus(LeaseStatus.EXPIRED).build()
    );
    Lease lease3 = leaseRepository.save(
        Lease.builder()
            .customer(infos.customer)
            .office(infos.office)
            .leaseStatus(LeaseStatus.EXPIRED).build()
    );
    Review review = Review.builder()
        .lease(infos.lease)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(1)
        .description("test1").build();
    Review review2 = Review.builder()
        .lease(lease2)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(2)
        .description("test2").build();
    Review review3 = Review.builder()
        .lease(lease3)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(3)
        .description("test3").build();

    reviewRepository.save(review);
    Thread.sleep(100);
    reviewRepository.save(review2);
    Thread.sleep(100);
    reviewRepository.save(review3);
    Thread.sleep(100);

    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
    //when
    Page<Review> reviews = reviewService.getReviewsByOfficeId(infos.office.getId(), pageable);
    List<Review> reviewList = reviews.getContent();
    //then

    for (var element : reviewList) {
      log.info("element = {}", element.getRate());
    }

    assertThat(reviewList.get(0).getDescription()).isEqualTo("test3");
  }

  @Test
  @DisplayName("리뷰 OverView 가져오기 성공")
  public void getOverView() {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    Lease lease2 = leaseRepository.save(
        Lease.builder()
            .customer(infos.customer)
            .office(infos.office)
            .leaseStatus(LeaseStatus.EXPIRED).build()
    );
    Lease lease3 = leaseRepository.save(
        Lease.builder()
            .customer(infos.customer)
            .office(infos.office)
            .leaseStatus(LeaseStatus.EXPIRED).build()
    );
    Review review = Review.builder()
        .lease(infos.lease)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(1)
        .description("test").build();
    Review review2 = Review.builder()
        .lease(lease2)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(2)
        .description("test").build();
    Review review3 = Review.builder()
        .lease(lease3)
        .customerId(infos.customer.getId())
        .officeId(infos.office.getId())
        .rate(3)
        .description("test").build();
    reviewRepository.save(review);
    reviewRepository.save(review2);
    reviewRepository.save(review3);
    //when
    ReviewOverviewDto dto = reviewService.getReviewOverviewByOfficeId(infos.office.getId());

    //then
    assertThat(dto.getReviewCount()).isEqualTo(3);
    assertThat(dto.getReviewRate()).isEqualTo(2);
  }

  private Infos makeInfos(String name, String email, String password, String roles, int point,
      LeaseStatus status) {
    Customer customer = customerRepository.saveAndFlush(
        Customer.builder()
            .name(name)
            .email(email)
            .password(password)
            .roles(Set.of(roles))
            .point(point).build()
    );
    Office office = officeRepository.saveAndFlush(
        Office.builder().name(name).build()
    );
    Lease lease = leaseRepository.saveAndFlush(
        Lease.builder()
            .customer(customer)
            .office(office)
            .leaseStatus(status).build()
    );

    return new Infos(customer, office, lease);
  }

  private void setOfficeInfo(OfficeCreateRequestDto request, String officeName, int maxCapacity,
      long leaseFee, int maxRoomCount) {
    request.setOfficeName(officeName);
    request.setMaxCapacity(maxCapacity);
    request.setLeaseFee(leaseFee);
    request.setMaxRoomCount(maxRoomCount);
  }

  private OfficeAddress setOfficeLocation(String legion, String city, String town, String detail,
      String street, int zipcode) {

    return OfficeAddress.builder()
        .legion(legion)
        .city(city)
        .town(town)
        .detail(detail)
        .street(street)
        .zipcode(String.valueOf(zipcode))
        .build();
  }

  private OfficeOption setOfficeCondition(boolean airCondition, boolean heaterCondition,
      boolean cafe,
      boolean printer, boolean packageSendService, boolean doorLock, boolean fax,
      boolean publicKitchen, boolean publicLounge, boolean privateLocker, boolean tvProjector,
      boolean whiteboard, boolean wifi, boolean showerBooth, boolean storage, boolean parkArea) {

    return OfficeOption.builder()
        .haveAirCondition(airCondition)
        .haveHeater(heaterCondition)
        .haveCafe(cafe)
        .havePrinter(printer)
        .packageSendServiceAvailable(packageSendService)
        .haveDoorLock(doorLock)
        .faxServiceAvailable(fax)
        .havePublicKitchen(publicKitchen)
        .havePublicLounge(publicLounge)
        .havePrivateLocker(privateLocker)
        .haveTvProjector(tvProjector)
        .haveWhiteBoard(whiteboard)
        .haveWifi(wifi)
        .haveShowerBooth(showerBooth)
        .haveStorage(storage)
        .haveParkArea(parkArea)
        .build();
  }

  private LeaseOfficeRequestDto createLeaseRequest(String email, Long officeId, LocalDate startDate,
      int months, int customerCount) {

    return LeaseOfficeRequestDto.builder()
        .email(email)
        .officeId(officeId)
        .startDate(startDate)
        .months(months)
        .customerCount(customerCount)
        .build();
  }

  private static class Infos {

    Customer customer;
    Office office;
    Lease lease;

    public Infos(Customer customer, Office office, Lease lease) {
      this.customer = customer;
      this.office = office;
      this.lease = lease;
    }
  }

}