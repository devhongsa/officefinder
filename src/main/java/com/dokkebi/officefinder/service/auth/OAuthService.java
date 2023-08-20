package com.dokkebi.officefinder.service.auth;

import com.dokkebi.officefinder.controller.auth.dto.Auth.LoginResponseCustomer;
import com.dokkebi.officefinder.entity.Customer;
import com.dokkebi.officefinder.exception.CustomErrorCode;
import com.dokkebi.officefinder.exception.CustomException;
import com.dokkebi.officefinder.repository.CustomerRepository;
import com.dokkebi.officefinder.security.TokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuthService {

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${spring.oauth2.google.client-id}")
  private String clientId;

  @Value("${spring.oauth2.google.client-secret}")
  private String secret;

  @Value("${spring.oauth2.google.redirect-uri}")
  private String redirectUri;

  @Value("${spring.oauth2.google.token-uri}")
  private String tokenUri;

  @Value("${spring.oauth2.google.resource-uri}")
  private String resourceUri;
  private final CustomerRepository customerRepository;

  private final TokenProvider tokenProvider;

  @Transactional
  public LoginResponseCustomer socialLogin(String code) {
    String accessToken = getAccessToken(code);
    JsonNode userResourceNode = getUserResource(accessToken);

    String email = userResourceNode.get("email").asText();
    String name = userResourceNode.get("name").asText();

    Customer customer = customerRepository.findByEmail(email)
        .orElseGet(() -> customerRepository.save(Customer.builder()
            .name(name)
            .email(email)
            .password("google")
            .point(0)
            .roles(Set.of("ROLE_CUSTOMER"))
            .build()));

    String token = tokenProvider.generateToken(customer.getId(), customer.getName(),
        "customer");

    return LoginResponseCustomer.builder()
        .customer(customer)
        .token(token)
        .build();
  }

  private String getAccessToken(String authorizationCode) {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", authorizationCode);
    params.add("client_id", clientId);
    params.add("client_secret", secret);
    params.add("redirect_uri", redirectUri);
    params.add("grant_type", "authorization_code");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity entity = new HttpEntity(params, headers);

    ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity,
        JsonNode.class);
    JsonNode accessTokenNode = responseNode.getBody();

    if (accessTokenNode.isNull()){
      throw new CustomException(CustomErrorCode.FAIL_LOGIN);
    }

    return accessTokenNode.get("access_token").asText();
  }

  private JsonNode getUserResource(String accessToken) {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity entity = new HttpEntity(headers);
    JsonNode userResource = restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class)
        .getBody();

    if (userResource.isNull()){
      throw new CustomException(CustomErrorCode.FAIL_LOGIN);
    }

    return userResource;
  }
}
