package com.dokkebi.officefinder.service.auth;

import com.dokkebi.officefinder.controller.auth.dto.Auth;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.repository.OfficeOwnerRepository;
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
}