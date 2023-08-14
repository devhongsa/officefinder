package com.dokkebi.officefinder.controller.auth;

import com.dokkebi.officefinder.controller.auth.dto.Auth;
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
        회원가입
        1. 회원가입에 필요한 정보들(SignUpCustomer Dto) Valid check
        2. 회원가입에 성공하면 SignUpResponse Dto 응답
     */
    @PostMapping("/customers/signup")
    public ResponseEntity<?> singUpUser(@RequestBody @Valid Auth.SignUpCustomer signupRequest){
        Auth.SignUpResponse signUpResponse = authService.register(signupRequest);
        log.info("user signup -> " + signUpResponse.getEmail());
        return ResponseEntity.ok(new ResponseDto<>("success",signUpResponse));
    }
}

