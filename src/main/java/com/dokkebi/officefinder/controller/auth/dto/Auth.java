package com.dokkebi.officefinder.controller.auth.dto;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Auth {

  /*
   * Customer 가입시 클라이언트 request Dto
   */
  @Getter
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

    public void encodePassword(PasswordEncoder passwordEncoder){
      this.password = passwordEncoder.encode(this.password);
    }
  }

  /*
    회원가입 성공시 응답 Dto
   */
  @Getter
  @NoArgsConstructor
  public static class SignUpResponseCustomer {
    private String name;

    @Builder
    public SignUpResponseCustomer(Customer customer) {
      this.name = customer.getName();
    }
  }

  /*
   * Customer 가입시 클라이언트 request Dto
   */
  @Getter
  @NoArgsConstructor
  public static class SignUpOfficeOwner {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String businessNumber;
    @NotBlank
    private String password;

    public OfficeOwner toEntity() {
      return OfficeOwner.builder()
          .name(name)
          .password(password)
          .email(email)
          .businessNumber(businessNumber)
          .build();
    }

    public void encodePassword(PasswordEncoder passwordEncoder){
      this.password = passwordEncoder.encode(this.password);
    }
  }

  /*
    회원가입 성공시 응답 Dto
  */
  @Getter
  @NoArgsConstructor
  public static class SignUpResponseOfficeOwner {
    private String name;

    @Builder
    public SignUpResponseOfficeOwner(OfficeOwner officeOwner) {
      this.name = officeOwner.getName();
    }
  }
}