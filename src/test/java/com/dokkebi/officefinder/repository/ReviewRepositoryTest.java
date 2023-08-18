package com.dokkebi.officefinder.repository;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.lease.Lease;
import com.dokkebi.officefinder.entity.office.Office;
import com.dokkebi.officefinder.entity.review.Review;
import com.dokkebi.officefinder.entity.type.LeaseStatus;
import com.dokkebi.officefinder.repository.lease.LeaseRepository;
import com.dokkebi.officefinder.repository.office.OfficeRepository;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
        .customer(customer)
        .office(office)
        .lease(lease)
        .rate(5)
        .description("a").build();
    Review savedReview = reviewRepository.save(review);
      //when
    boolean found = reviewRepository.existsByLeaseId(savedReview.getLease().getId());
      //then
    Assertions.assertTrue(found);
  }

}