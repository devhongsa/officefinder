package com.dokkebi.officefinder.service;

import com.dokkebi.officefinder.repository.ReviewRepository;
import com.dokkebi.officefinder.service.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReviewServiceTest {

  @Autowired
  private ReviewService reviewService;
  @Autowired
  private ReviewRepository reviewRepository;




}