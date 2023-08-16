package com.dokkebi.officefinder.service.auth;

import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailService {

  private final CustomerRepository customerRepository;
  private final OfficeOwnerRepository officeOwnerRepository;

  public UserDetails loadUserById(String userType, Long id) throws UsernameNotFoundException {
    if (userType.equals("customer")) {
      return customerRepository.findById(id)
          .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    } else {
      return officeOwnerRepository.findById(id)
          .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }
  }

}
