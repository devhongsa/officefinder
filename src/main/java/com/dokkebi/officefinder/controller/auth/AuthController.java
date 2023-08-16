package com.dokkebi.officefinder.controller.auth;

import com.dokkebi.officefinder.controller.auth.dto.Auth;
import com.dokkebi.officefinder.controller.auth.dto.Auth.LoginResponseCustomer;
import com.dokkebi.officefinder.controller.auth.dto.Auth.LoginResponseOfficeOwner;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/*
    회원가입, 로그인 api
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    /*
        Customer 회원가입
        1. 회원가입에 필요한 정보들(SignUpCustomer Dto) Valid check
        2. 회원가입에 성공하면 SignUpResponse Dto 응답
     */
    @PostMapping("/customers/signup")
    public ResponseEntity<?> singUpUser(@RequestBody @Valid Auth.SignUpCustomer signupRequest){
        Auth.SignUpResponseCustomer signUpResponse = authService.register(signupRequest);
        log.info("customer signup -> " + signUpResponse.getName());
        return ResponseEntity.ok(new ResponseDto<>("success",signUpResponse));
    }

    /*
        OfficeOwner 회원가입
        1. 회원가입에 필요한 정보들(SignUpOfficeOwner Dto) Valid check
        2. 회원가입에 성공하면 SignUpResponse Dto 응답
     */
    @PostMapping("/agents/signup")
    public ResponseEntity<?> singUpUser(@RequestBody @Valid Auth.SignUpOfficeOwner signupRequest){
        Auth.SignUpResponseOfficeOwner signUpResponse = authService.register(signupRequest);
        log.info("agent signup -> " + signUpResponse.getName());
        return ResponseEntity.ok(new ResponseDto<>("success",signUpResponse));
    }

    /*
        Customer 로그인
        1. 클라이언트로부터 이메일, 비밀번호를 받아 Valid 체크
        2. 로그인에 성공하면 LoginReponse Dto 객체 응답
     */
    @PostMapping("/customers/login")
    public ResponseEntity<?> loginCustomer(@RequestBody @Valid Auth.SignIn loginRequest) {
        LoginResponseCustomer customer = authService.loginCustomer(loginRequest);

        return ResponseEntity.ok(new ResponseDto<>("success",customer));
    }

    /*
        OfficeOwner 로그인
        1. 클라이언트로부터 이메일, 비밀번호를 받아 Valid 체크
        2. 로그인에 성공하면 LoginReponse Dto 객체 응답
     */
    @PostMapping("/agents/login")
    public ResponseEntity<?> loginOfficeOwner(@RequestBody @Valid Auth.SignIn loginRequest) {
        LoginResponseOfficeOwner officeOwner = authService.loginOfficeOwner(loginRequest);

        return ResponseEntity.ok(new ResponseDto<>("success",officeOwner));
    }
}

