package com.dokkebi.officefinder.service.auth;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

import com.dokkebi.officefinder.controller.auth.dto.Auth;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private OfficeOwnerRepository officeOwnerRepository;
  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthService authService;

  @Test
  @DisplayName("Customer 회원가입 성공")
  void registerCustomer_Success() {
    // given
    Auth.SignUpCustomer customer = new Auth.SignUpCustomer("honghong","bippr@gmail.com","password");

    given(customerRepository.existsByEmail(anyString()))
        .willReturn(false);

    given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

    given(customerRepository.save(any(Customer.class)))
        .willReturn(Customer.builder()
            .name("hong")
            .email("bippr@gmail.com")
            .password("encodedPassword")
            .point(0)
            .roles(Set.of("ROLE_CUSTOMER"))
            .build());

    ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);


    // when
    Auth.SignUpResponseCustomer response = authService.register(customer);

    // then
    Mockito.verify(customerRepository, times(1)).save(captor.capture());
    assertEquals("hong", response.getName());
    assertEquals("honghong",captor.getValue().getName());
    assertEquals("bippr@gmail.com",captor.getValue().getEmail());
    assertEquals("encodedPassword", captor.getValue().getPassword());
  }

  @Test
  @DisplayName("Customer 회원가입 실패 - 이미 등록된 이메일")
  void registerCustomer_Fail(){
    //given
    Auth.SignUpCustomer customer = new Auth.SignUpCustomer("hong","bippr@gmail.com","password");

    given(customerRepository.existsByEmail(anyString()))
        .willReturn(true);

    //when
    CustomException exception = assertThrows(CustomException.class,
        () -> authService.register(customer));

    //then
    assertEquals(CustomErrorCode.EMAIL_ALREADY_REGISTERED, exception.getErrorCode());
  }

  @Test
  @DisplayName("OfficeOwner 회원가입 성공")
  void registerOfficeOwner_Success() {
    // given
    Auth.SignUpOfficeOwner officeOwner = new Auth.SignUpOfficeOwner("hongOwner","bippr@gmail.com","12345","password");

    given(officeOwnerRepository.existsByEmail(anyString()))
        .willReturn(false);

    given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

    given(officeOwnerRepository.save(any(OfficeOwner.class)))
        .willReturn(OfficeOwner.builder()
            .name("hong")
            .email("bippr@gmail.com")
            .password("encodedPassword")
            .businessNumber("12345")
            .point(0)
            .roles(Set.of("ROLE_OFFICE_OWNER"))
            .build());

    ArgumentCaptor<OfficeOwner> captor = ArgumentCaptor.forClass(OfficeOwner.class);

    // when
    Auth.SignUpResponseOfficeOwner response = authService.register(officeOwner);

    // then
    Mockito.verify(officeOwnerRepository, times(1)).save(captor.capture());
    assertEquals("hong", response.getName());
    assertEquals("hongOwner",captor.getValue().getName());
    assertEquals("bippr@gmail.com",captor.getValue().getEmail());
    assertEquals("12345",captor.getValue().getBusinessNumber());
    assertEquals("encodedPassword", captor.getValue().getPassword());
  }

  @Test
  @DisplayName("OfficeOwner 회원가입 실패 - 이미 등록된 이메일")
  void registerOfficeOwner_Fail(){
    //given
    Auth.SignUpOfficeOwner officeOwner = new Auth.SignUpOfficeOwner("hong","bippr@gmail.com", "12345","password");

    given(officeOwnerRepository.existsByEmail(anyString()))
        .willReturn(true);

    //when
    CustomException exception = assertThrows(CustomException.class,
        () -> authService.register(officeOwner));

    //then
    assertEquals(CustomErrorCode.EMAIL_ALREADY_REGISTERED, exception.getErrorCode());
  }
}