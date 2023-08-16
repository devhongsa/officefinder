package com.dokkebi.officefinder.controller.auth.dto;

import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.entity.OfficeOwner;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
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
  @AllArgsConstructor
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
          .point(0)
          .roles(Set.of("ROLE_CUSTOMER"))
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
  @AllArgsConstructor
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
          .point(0)
          .roles(Set.of("ROLE_OFFICE_OWNER"))
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


  /*
   * 회원 로그인시 클라이언트 request Dto
   */
  @Getter
  @AllArgsConstructor
  public static class SignIn {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
  }

  /*
    로그인 성공시 응답 Dto
  */
  @Getter
  @NoArgsConstructor
  public static class LoginResponseCustomer {
    private String name;
    private String email;
    private long point;
    private String userType;
    private String token;

    @Builder
    public LoginResponseCustomer(Customer customer, String token) {
      this.name = customer.getName();
      this.email = customer.getEmail();
      this.point = customer.getPoint();
      this.token = token;
      this.userType = "customer";
    }

  }

  /*
    로그인 성공시 응답 Dto
  */
  @Getter
  @NoArgsConstructor
  public static class LoginResponseOfficeOwner {
    private String name;
    private String email;
    private long point;
    private String userType;
    private String token;

    @Builder
    public LoginResponseOfficeOwner(OfficeOwner officeOwner, String token) {
      this.name = officeOwner.getName();
      this.email = officeOwner.getEmail();
      this.point = officeOwner.getPoint();
      this.token = token;
      this.userType = "agent";
    }

  }

}