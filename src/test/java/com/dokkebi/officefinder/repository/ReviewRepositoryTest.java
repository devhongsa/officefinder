package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DataJpaTest
public class ReviewRepositoryTest {

  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private OfficeRepository officeRepository;
  @Autowired
  private LeaseRepository leaseRepository;

  @Test
  @DisplayName("leaseId로 리뷰를 찾을 수 있다.")
  public void existsByLeaseId() throws Exception {
      //given
    Customer customer = customerRepository.save(Customer.builder().name("1").email("").password("").roles(
        Set.of("a")).point(0).build());
    Office office = officeRepository.save(Office.builder().name("1").build());
    Lease lease = leaseRepository.save(Lease.builder().office(office).customer(customer).leaseStatus(
        LeaseStatus.EXPIRED).build());

    Review review = Review.builder()
        .lease(lease)
        .rate(5)
        .description("a").build();
    Review savedReview = reviewRepository.save(review);
      //when
    boolean found = reviewRepository.existsByLeaseId(savedReview.getLease().getId());
      //then
    Assertions.assertTrue(found);
  }

  @Test
  @DisplayName("officeId로 리뷰를 등록 순서대로 찾을 수 있다.")
  public void findByOfficeId() throws InterruptedException {
    //given
    Customer customer = customerRepository.save(Customer.builder().name("1").email("").password("").roles(
        Set.of("a")).point(0).build());
    Office office = officeRepository.save(Office.builder().name("1").build());
    Lease lease = leaseRepository.save(Lease.builder().office(office).customer(customer).leaseStatus(
        LeaseStatus.EXPIRED).build());

    Review review = Review.builder()
        .lease(lease)
        .customerId(customer.getId())
        .officeId(office.getId())
        .rate(5)
        .description("1").build();
    Review savedReview = reviewRepository.save(review);

    Customer customer2 = customerRepository.save(Customer.builder().name("2").email("2").password("").roles(
        Set.of("a")).point(0).build());
    Lease lease2 = leaseRepository.save(Lease.builder().office(office).customer(customer2).leaseStatus(
        LeaseStatus.EXPIRED).build());

    Review review2 = Review.builder()
        .lease(lease2)
        .customerId(customer2.getId())
        .officeId(office.getId())
        .rate(5)
        .description("2").build();
    Review savedReview2 = reviewRepository.save(review2);
    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
    //when
    Page<Review> reviews = reviewRepository.findByOfficeId(
        office.getId(), pageable);
    List<Review> reviewList = reviews.getContent();
    //then
    Assertions.assertEquals(reviewList.get(0).getDescription(), "2");
  }


}