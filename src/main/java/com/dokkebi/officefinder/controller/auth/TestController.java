package com.dokkebi.officefinder.controller.auth;

import com.dokkebi.officefinder.security.TokenProvider;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TestController {

  private final TokenProvider tokenProvider;

  @PreAuthorize(("hasRole('OFFICE_OWNER')"))
//  @PreAuthorize("hasRole('CUSTOMER')")
  @GetMapping("/test")
  public String test(HttpServletRequest request, @CookieValue("Authorization") String jwt) {
    // 유저 email 추출
    String email = request.getUserPrincipal().getName();
    System.out.println(email);
    // 유저 id 추출
    Long id = tokenProvider.getUserId(jwt);
    System.out.println(id);
    return "success";
  }
}
