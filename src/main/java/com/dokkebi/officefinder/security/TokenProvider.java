package com.dokkebi.officefinder.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24; // 24 hour

  @Value("${spring.jwt.secret}")
  private String secretKey;

  public String generateToken(Long id, String username, String userType) {
    Claims claims = Jwts.claims().setSubject(userType);
    claims.put("id", id);
    claims.put("name", username);

    Date now = new Date();
    Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expiredDate)
        .signWith(SignatureAlgorithm.HS512, secretKey)
        .compact();
  }
}
