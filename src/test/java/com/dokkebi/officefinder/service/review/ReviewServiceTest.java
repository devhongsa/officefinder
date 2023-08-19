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
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
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

  @AfterEach
  void tearDown() {
    reviewRepository.deleteAllInBatch();
    leaseRepository.deleteAllInBatch();
    customerRepository.deleteAllInBatch();
    officeRepository.deleteAllInBatch();
  }

  private Lease makeLease(String name, String email, String password, String roles, int point, LeaseStatus status) {
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
    return leaseRepository.save(
        Lease.builder()
            .customer(customer)
            .office(office)
            .leaseStatus(status).build()
    );
  }

  @Test
  @DisplayName("Submit시 leaseId가 DB에 없는 경우 exception을 리턴한다.")
  public void ReviewSubmit1() throws Exception {
    //given
    Lease lease = makeLease("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();
    //then
    Assertions.assertThatThrownBy(
        () -> reviewService.submit(submitControllerRequest, "test@naver.com", lease.getId()+1)
    ).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Submit시 lease 내부의 회원 이메일과 submit의 회원 이메일이 다른 경우 exception return")
  public void ReviewSubmit2() throws Exception {
    //given
    Lease lease = makeLease("1", "false@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();
    //then
    Assertions.assertThatThrownBy(
        () -> reviewService.submit(submitControllerRequest, "test@naver.com", lease.getId())
    ).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Submit시 이미 동일한 임대에 대한 리뷰가 존재하는 경우 exception return")
  public void ReviewSubmit3() throws Exception {
    //given
    Lease lease = makeLease("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();
    reviewRepository.save(Review.builder().lease(lease).build());
    //then
    Assertions.assertThatThrownBy(
        () -> reviewService.submit(submitControllerRequest, "test@naver.com", lease.getId())
    ).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Submit시 lease 상태가 EXPIRED가 아닌 경우 exception return")
  public void ReviewSubmit4() throws Exception {
    //given
    Lease lease = makeLease("1", "test@naver.com", "1", "customer", 0, LeaseStatus.AWAIT);
    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();
    //then
    Assertions.assertThatThrownBy(
        () -> reviewService.submit(submitControllerRequest, "test@naver.com", lease.getId())
    ).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Submit 정상 작동하는 경우")
  public void ReviewSubmit5() throws Exception {
    //given
    Lease lease = makeLease("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();
    //when
    Review review = reviewService.submit(submitControllerRequest, "test@naver.com", lease.getId());
    //then
    Assertions.assertThat(review.getLease().getId())
        .isEqualTo(lease.getId());
  }

  @Test
  @DisplayName("Update시 reviewId가 존재하지 않으면 exception 리턴")
  public void ReviewUpdate1() throws Exception {
    //given
    Lease lease = makeLease("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    Review review = Review.builder()
        .lease(lease)
        .rate(5)
        .description("test").build();
    Review savedReview = reviewRepository.save(review);

    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();

    //then
    Assertions.assertThatThrownBy(() -> reviewService.update(submitControllerRequest,
            "test@naver.com", savedReview.getId() + 1))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Update시 요청하는 회원과 리뷰 작성자가 다를 경우 exception 리턴")
  public void ReviewUpdate2() throws Exception {
    Lease lease = makeLease("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    Review review = Review.builder()
        .lease(lease)
        .rate(5)
        .description("test").build();
    Review savedReview = reviewRepository.save(review);

    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(5)
        .description("테스트").build();

    //then
    Assertions.assertThatThrownBy(() -> reviewService.update(submitControllerRequest,
            "false@naver.com", savedReview.getId() + 1))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Update시 정상 작동")
  public void ReviewUpdate3() throws Exception {
    Lease lease = makeLease("1", "test@naver.com", "1", "customer", 0, LeaseStatus.EXPIRED);
    Review review = Review.builder()
        .lease(lease)
        .rate(5)
        .description("test").build();
    Review savedReview = reviewRepository.save(review);

    SubmitControllerRequest submitControllerRequest = SubmitControllerRequest.builder()
        .rate(1)
        .description("수정 후").build();
    //when
    Review updatedReview = reviewService.update(submitControllerRequest,
        "test@naver.com", savedReview.getId());
    //then
    Assertions.assertThat(updatedReview.getRate()).isEqualTo(1);
    Assertions.assertThat(updatedReview.getDescription()).isEqualTo("수정 후");
  }

}