package com.dokkebi.officefinder.service.customer;

import static com.dokkebi.officefinder.exception.CustomErrorCode.USER_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.dokkebi.officefinder.controller.customer.dto.CustomerControllerDto.CustomerInfo;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.PointChargeHistory;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.history.ChargeHistoryRepository;
import com.dokkebi.officefinder.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final ChargeHistoryRepository chargeHistoryRepository;

  @Transactional
  public void chargeCustomerPoint(long amount, String customerEmail){
    Customer customer = customerRepository.findByEmail(customerEmail)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND, USER_NOT_FOUND.getErrorMessage(),
            BAD_REQUEST));

    PointChargeHistory history = PointChargeHistory.fromRequest(customer, amount);
    chargeHistoryRepository.save(history);

    customer.chargePoint(amount);
  }

  public CustomerInfo getCustomerInfo(Long id) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND, USER_NOT_FOUND.getErrorMessage(),
            BAD_REQUEST));
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.DESC, "createdAt"));
    Page<PointChargeHistory> chargeHistories = chargeHistoryRepository.findByCustomerEmail(
        customer.getEmail(), pageable);

    return CustomerInfo.builder()
        .id(customer.getId())
        .email(customer.getEmail())
        .name(customer.getName())
        .point(customer.getPoint())
        .roles(customer.getRoles())
        .pointChargeHistories(chargeHistories)
        .build();
  }
}