package com.dokkebi.officefinder.service.review;

import com.dokkebi.officefinder.controller.review.dto.ReviewControllerDto.SubmitControllerRequest;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.repository.office.condition.OfficeConditionRepository;
import com.dokkebi.officefinder.repository.office.location.OfficeLocationRepository;
import com.dokkebi.officefinder.repository.office.picture.OfficePictureRepository;
import com.dokkebi.officefinder.service.review.dto.ReviewOverviewDto;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    Assertions.assertThatThrownBy(
        () -> reviewService.submit(submitControllerRequest, infos.customer.getId(), infos.lease.getId()+1)
    ).isInstanceOf(IllegalArgumentException.class);
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
    Assertions.assertThatThrownBy(
        () -> reviewService.submit(submitControllerRequest, infos.customer.getId()+1, infos.lease.getId())
    ).isInstanceOf(IllegalArgumentException.class);
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
    Assertions.assertThatThrownBy(
        () -> reviewService.submit(submitControllerRequest, infos.customer.getId(), infos.lease.getId())
    ).isInstanceOf(IllegalArgumentException.class);
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
    Assertions.assertThatThrownBy(
        () -> reviewService.submit(submitControllerRequest, infos.customer.getId(), infos.lease.getId())
    ).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Submit 정상 작동하는 경우")
  public void ReviewSubmit5() throws Exception {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();
    //then
    Assertions.assertThat(reviewService.submit
                (submitControllerRequest, infos.customer.getId(), infos.lease.getId())
            .getCustomerId())
        .isEqualTo(infos.customer.getId());
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
    Assertions.assertThatThrownBy(() -> reviewService.update(submitControllerRequest,
            infos.customer.getId(), savedReview.getId() + 1))
        .isInstanceOf(IllegalArgumentException.class);
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
    Assertions.assertThatThrownBy(() -> reviewService.update(submitControllerRequest,
            infos.customer.getId()+1, savedReview.getId()))
        .isInstanceOf(IllegalArgumentException.class);
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
    Assertions.assertThat(updatedReview.getId()).isEqualTo(savedReview.getId());
    Assertions.assertThat(updatedReview.getRate()).isEqualTo(1);
    Assertions.assertThat(updatedReview.getDescription()).isEqualTo("수정 후");
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
    Assertions.assertThatThrownBy(() -> reviewService.getReviewsByCustomerId(infos.customer.getId() + 1, pageable))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("read할때 임대계약은 있지만 리뷰는 없는 경우")
  public void getReviews2() {

    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);

    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

    //then
    Assertions.assertThatThrownBy(() -> reviewService.getReviewsByCustomerId(infos.office.getId(), pageable))
        .isInstanceOf(IllegalArgumentException.class);
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
    Assertions.assertThat(reviewList.get(0).getLease().getCustomer().getName()).isEqualTo("1");
    Assertions.assertThat(reviewList.get(1).getLease().getCustomer().getName()).isEqualTo("1");
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
    Assertions.assertThatThrownBy(() -> reviewService.delete(infos.customer.getId()+1, savedReview.getId())).message()
        .isEqualTo("회원이 존재하지 않습니다.");
  }

  @Test
  @DisplayName("delete할때 review가 존재하지 않는 경우")
  public void delete2() {
    //given
    Infos infos = makeInfos("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);

    //then
    Assertions.assertThatThrownBy(() -> reviewService.delete(infos.customer.getId(), 1L))
        .message().isEqualTo("리뷰가 존재하지 않습니다.");
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
    Assertions.assertThatThrownBy(() -> reviewService.delete(infos2.customer.getId(), review1.getId()))
        .message().isEqualTo("리뷰 작성자 본인이 아닙니다.");
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
    Assertions.assertThat(reviewRepository.findById(savedReview.getId())).isEmpty();
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

    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
    //when
    Page<Review> reviews = reviewService.getReviewsByOfficeId(infos.office.getId(), pageable);
    List<Review> reviewList = reviews.getContent();
    //then

    for (var element: reviewList){
      log.info("element = {}", element.getRate());
    }

    Assertions.assertThat(reviewList.get(0).getDescription()).isEqualTo("test3");
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
    Assertions.assertThat(dto.getReviewCount()).isEqualTo(3);
    Assertions.assertThat(dto.getReviewRate()).isEqualTo(2);
  }

  private Infos makeInfos(String name, String email, String password, String roles, int point, LeaseStatus status) {
    Customer customer = customerRepository.save(
        Customer.builder()
            .name(name)
            .email(email)
            .password(password)
            .roles(Set.of(roles))
            .point(point).build()
    );
    Office office = officeRepository.save(
        Office.builder().name(name).build()
    );
    Lease lease = leaseRepository.save(
        Lease.builder()
            .customer(customer)
            .office(office)
            .leaseStatus(status).build()
    );

    return new Infos(customer, office, lease);
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