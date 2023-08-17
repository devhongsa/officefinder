package com.dokkebi.officefinder.service;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.LeaseRepository;
import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import com.dokkebi.officefinder.service.review.ReviewService;
import com.dokkebi.officefinder.service.review.dto.ReviewServiceDto.SubmitServiceRequest;
import com.dokkebi.officefinder.service.review.dto.ReviewServiceDto.SubmitServiceResponse;
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

  @Test
  @DisplayName("leaseId가 DB에 없는 경우 exception을 리턴한다.")
  public void ReviewSubmit1() throws Exception {
    //given
    Customer customer = customerRepository.save(Customer.builder().name("1").email("test@naver.com").password("").roles(
        Set.of("a")).point(0).build());
    Office office = officeRepository.save(Office.builder().name("1").build());
    Lease lease = leaseRepository.save(Lease.builder().customer(customer).office(office).leaseStatus(
        LeaseStatus.EXPIRED).build());
    SubmitServiceRequest submitServiceRequest = SubmitServiceRequest.builder()
        .customerEmail("test@naver.com")
        .leaseId(lease.getId() + 1)
        .rate(5)
        .description("테스트").build();
    //then
    Assertions.assertThatThrownBy(() -> reviewService.submit(submitServiceRequest)).isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("lease 내부의 회원 이메일과 submit의 회원 이메일이 다른 경우 exception return")
  public void ReviewSubmit2() throws Exception {
    //given
    Customer customer2 = customerRepository.save(Customer.builder().name("2").email("falseemail@naver.com").password("").roles(
        Set.of("a")).point(0).build());
    Office office2 = officeRepository.save(Office.builder().name("2").build());
    Lease lease2 = leaseRepository.save(Lease.builder().customer(customer2).office(office2).leaseStatus(
        LeaseStatus.EXPIRED).build());
    SubmitServiceRequest submitServiceRequest2 = SubmitServiceRequest.builder()
        .customerEmail("test2@naver.com")
        .leaseId(lease2.getId())
        .rate(5)
        .description("테스트").build();
    //then
    Assertions.assertThatThrownBy(() -> reviewService.submit(submitServiceRequest2)).isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("이미 동일한 회원의 오피스에 대한 리뷰가 존재하는 경우 exception return")
  public void ReviewSubmit3() throws Exception {
    //given
    Customer customer3 = customerRepository.save(Customer.builder().name("3").email("test3@naver.com").password("").roles(
        Set.of("a")).point(0).build());
    Office office3 = officeRepository.save(Office.builder().name("3").build());
    Lease lease3 = leaseRepository.save(Lease.builder().customer(customer3).office(office3).leaseStatus(
        LeaseStatus.EXPIRED).build());
    SubmitServiceRequest submitServiceRequest3 = SubmitServiceRequest.builder()
        .customerEmail("test3@naver.com")
        .leaseId(lease3.getId())
        .rate(5)
        .description("테스트").build();
    reviewRepository.save(Review.builder().customer(customer3).office(office3).lease(lease3).build());
    //then
    Assertions.assertThatThrownBy(() -> reviewService.submit(submitServiceRequest3)).isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("lease 상태가 EXPIRED가 아닌 경우 exception return")
  public void ReviewSubmit4() throws Exception {
    //given
    Customer customer4 = customerRepository.save(Customer.builder().name("4").email("test4@naver.com").password("").roles(
        Set.of("a")).point(0).build());
    Office office4 = officeRepository.save(Office.builder().name("4").build());
    Lease lease4 = leaseRepository.save(Lease.builder().customer(customer4).office(office4).leaseStatus(
        LeaseStatus.AWAIT).build());
    SubmitServiceRequest submitServiceRequest4 = SubmitServiceRequest.builder()
        .customerEmail("test4@naver.com")
        .leaseId(lease4.getId())
        .rate(5)
        .description("테스트").build();
    //then
    Assertions.assertThatThrownBy(() -> reviewService.submit(submitServiceRequest4)).isInstanceOf(CustomException.class);
  }

  @Test
  @DisplayName("정상 작동하는 경우")
  public void ReviewSubmit5() throws Exception {
    //given
    Customer customer5 = customerRepository.save(Customer.builder().name("5").email("test5@naver.com").password("").roles(
        Set.of("a")).point(0).build());
    Office office5 = officeRepository.save(Office.builder().name("5").build());
    Lease lease5 = leaseRepository.save(Lease.builder().customer(customer5).office(office5).leaseStatus(
        LeaseStatus.EXPIRED).build());
    SubmitServiceRequest submitServiceRequest5 = SubmitServiceRequest.builder()
        .customerEmail("test5@naver.com")
        .leaseId(lease5.getId())
        .rate(5)
        .description("테스트").build();
    //then
    SubmitServiceResponse submitServiceResponse = reviewService.submit(submitServiceRequest5);
    Assertions.assertThat(submitServiceResponse.getCustomerName())
        .isEqualTo(customer5.getName());
    Assertions.assertThat(submitServiceResponse.getOfficeName())
        .isEqualTo(office5.getName());
  }

}