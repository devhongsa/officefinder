package com.dokkebi.officefinder.service.customer;

import static com.dokkebi.officefinder.exception.CustomErrorCode.USER_NOT_FOUND;

import com.dokkebi.officefinder.controller.customer.dto.CustomerControllerDto.CustomerInfo;
import com.dokkebi.officefinder.controller.customer.dto.PointChargeHistoryDto;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.PointChargeHistory;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.history.ChargeHistoryRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final ChargeHistoryRepository chargeHistoryRepository;

  @Transactional
  public void chargeCustomerPoint(long amount, String customerEmail) {
    Customer customer = customerRepository.findByEmail(customerEmail)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    PointChargeHistory history = PointChargeHistory.fromRequest(customer, amount);
    chargeHistoryRepository.save(history);

    customer.chargePoint(amount);
  }

  @Transactional
  public CustomerInfo getCustomerInfo(Long id) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    Set<PointChargeHistory> histories = chargeHistoryRepository.findTop10ByCustomerIdOrderByCreatedAtDesc(
        id);
    Set<PointChargeHistoryDto> historyDtoSet = toDtoSet(histories);

    return CustomerInfo.builder()
        .id(customer.getId())
        .email(customer.getEmail())
        .name(customer.getName())
        .point(customer.getPoint())
        .roles(customer.getRoles())
        .histories(historyDtoSet)
        .build();
  }

  @Transactional
  public Page<PointChargeHistory> getAllHistories(Long id, Pageable pageable) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    return chargeHistoryRepository.findByCustomerIdOrderByCreatedAtDesc(id, pageable);
  }

  @Transactional
  public void changeCustomerProfileImage(String imagePath, String userEmail) {
    Customer customer = customerRepository.findByEmail(userEmail)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    customer.changeProfileImage(imagePath);
  }

  private Set<PointChargeHistoryDto> toDtoSet(Set<PointChargeHistory> histories) {

    return histories.stream().map(o -> PointChargeHistoryDto.builder()
            .chargeAmount(o.getChargeAmount()).createdAt(o.getCreatedAt()).build())
        .collect(Collectors.toSet());
  }
}