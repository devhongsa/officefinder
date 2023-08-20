package com.dokkebi.officefinder.service.auth;

import com.dokkebi.officefinder.controller.auth.dto.Auth;
import com.dokkebi.officefinder.controller.auth.dto.Auth.LoginResponseCustomer;
import com.dokkebi.officefinder.controller.auth.dto.Auth.LoginResponseOfficeOwner;
import com.dokkebi.officefinder.controller.auth.dto.Auth.SignIn;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
import com.dokkebi.officefinder.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final CustomerRepository customerRepository;
  private final OfficeOwnerRepository officeOwnerRepository;
  private final TokenProvider tokenProvider;
  private final PasswordEncoder passwordEncoder;

  /*
      회원 등록
      - register 메소드 오버로딩
      1. client가 요청보낸 회원가입 정보를 가져와서 이미 가입된 email인지 확인
      2. 이미 등록된 email이면 예외발생
      3. 중복이 없으면 비밀번호는 암호화 처리 후 setPassword
      4. SignUpDto 객체를 Entity객체로 변환 후 DB에 저장(회원가입 완료)
      5. SignUpResponse Dto를 controller에 리턴
   */
  @Transactional
  public Auth.SignUpResponseCustomer register(Auth.SignUpCustomer signupRequest) {
    if (customerRepository.existsByEmail(signupRequest.getEmail())) {
      throw new CustomException(CustomErrorCode.EMAIL_ALREADY_REGISTERED);
    }

    signupRequest.encodePassword(passwordEncoder);
    Customer customer = customerRepository.save(signupRequest.toEntity());
    return Auth.SignUpResponseCustomer.builder().customer(customer).build();
  }


  @Transactional
  public Auth.SignUpResponseOfficeOwner register(Auth.SignUpOfficeOwner signupRequest) {
    if (officeOwnerRepository.existsByEmail(signupRequest.getEmail())) {
      throw new CustomException(CustomErrorCode.EMAIL_ALREADY_REGISTERED);
    }

    signupRequest.encodePassword(passwordEncoder);
    OfficeOwner officeOwner = officeOwnerRepository.save(signupRequest.toEntity());
    return Auth.SignUpResponseOfficeOwner.builder().officeOwner(officeOwner).build();
  }

  /*
      Customer 로그인
      1. client에서 보내온 이메일, 비밀번호는 일치하는지 확인
      2. jwt 토큰 발급
      3. LoginReponse Dto 객체에 로그인 회원 정보와 jwt토큰을 실어서 controller에 전달
   */
  @Transactional
  public LoginResponseCustomer loginCustomer(Auth.SignIn request) {

    Customer customer = customerRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_REGISTERED));

    if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
      throw new CustomException(CustomErrorCode.PASSWORD_NOT_MATCH);
    }

    String token = tokenProvider.generateToken(customer.getId(), customer.getName(), "customer");

    return LoginResponseCustomer.builder()
        .customer(customer)
        .token(token)
        .build();
  }

  /*
      OfficeOwner 로그인
      1. client에서 보내온 이메일, 비밀번호는 일치하는지 확인
      2. jwt 토큰 발급
      3. LoginReponse Dto 객체에 로그인 회원 정보와 jwt토큰을 실어서 controller에 전달
   */
  @Transactional
  public LoginResponseOfficeOwner loginOfficeOwner(SignIn request) {
    OfficeOwner officeOwner = officeOwnerRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_REGISTERED));

    if (!passwordEncoder.matches(request.getPassword(), officeOwner.getPassword())) {
      throw new CustomException(CustomErrorCode.PASSWORD_NOT_MATCH);
    }

    String token = tokenProvider.generateToken(officeOwner.getId(), officeOwner.getName(), "agent");

    return LoginResponseOfficeOwner.builder()
        .officeOwner(officeOwner)
        .token(token)
        .build();
  }
}