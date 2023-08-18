package com.dokkebi.officefinder.controller.auth;

import com.dokkebi.officefinder.controller.auth.dto.Auth.LoginResponseCustomer;
import com.dokkebi.officefinder.dto.ResponseDto;
import com.dokkebi.officefinder.service.auth.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class OAuthController {

  private final OAuthService oAuthService;

  @Value("${spring.oauth2.google.client-id}")
  private String clientId;

  @Value("${spring.oauth2.google.redirect-uri}")
  private String redirectUri;

  @GetMapping("api/customers/login/oauth2/google")
  public RedirectView googleLogin() {
    String newUrl = String.format("https://accounts.google.com/o/oauth2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/userinfo.profile",clientId,redirectUri);

    System.out.println(newUrl);

    RedirectView redirectView = new RedirectView();
    redirectView.setUrl(newUrl);

    return redirectView;
  }

  @GetMapping("/login/oauth2/code/google")
  public ResponseDto<?> googleLogin(@RequestParam String code) {
    LoginResponseCustomer customer = oAuthService.socialLogin(code);

    return new ResponseDto<>("success",customer);
  }
}
