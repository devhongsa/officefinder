package com.dokkebi.officefinder.service.customer;

import static com.dokkebi.officefinder.exception.CustomErrorCode.USER_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.PointChargeHistory;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.history.ChargeHistoryRepository;
import com.dokkebi.officefinder.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
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
}
