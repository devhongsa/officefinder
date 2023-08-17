package com.dokkebi.officefinder.security;

import com.dokkebi.officefinder.service.auth.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.util.Date;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24; // 24 hour

  private final UserDetailService userDetailService;

  @Value("${spring.jwt.secret}")
  private String secretKey;

  @Value("${spring.jwt.token-prefix}")
  private String tokenPrefix;

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

  public Authentication getAuthentication(String token) {
    UserDetails userDetails = userDetailService.loadUserById(this.getUserType(token),this.getUserId(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getUserType(String token) {
    return parseClaims(token).getSubject();
  }

  public Long getUserId(String token) {
    return parseClaims(token).get("id", Long.class);
  }

  public Long getUserIdFromHeader(String header) {
    return this.getUserId(this.resolveTokenFromHeader(header));
  }

  public boolean validateToken(String token) {
    if (!StringUtils.hasText(token)) {
      return false; //빈문자열인지 체크
    }

    Claims claims = parseClaims(token);
    return !claims.getExpiration().before(new Date()); // 만기가 지난 토큰인지 체크
  }

  private Claims parseClaims(String token) {
    try {
      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  public String resolveTokenFromHeader(String header) {
    if (!ObjectUtils.isEmpty(header) && header.startsWith(tokenPrefix)) {
      return header.substring(tokenPrefix.length());
    }
    return null;
  }
}
