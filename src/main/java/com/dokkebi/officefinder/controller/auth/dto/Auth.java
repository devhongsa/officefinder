package com.dokkebi.officefinder.controller.auth.dto;

import com.dokkebi.officefinder.entity.Customer;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Auth {

  /*
   * Customer 가입시 클라이언트 request Dto
   */
  @Getter
  @Setter
  @NoArgsConstructor
  public static class SignUpCustomer {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;

    public Customer toEntity() {
      return Customer.builder()
          .name(name)
          .password(password)
          .email(email)
          .build();
    }
  }

  /*
    회원가입 성공시 응답 Dto
   */
  @Data
  @AllArgsConstructor
  public static class SignUpResponse {
    private String name;
    private String email;

    @Builder
    public SignUpResponse(Customer customer) {
      this.name = customer.getName();
      this.email = customer.getEmail();
    }
  }
}