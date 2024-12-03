package com.nookbook.global.config.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${app.auth.token-secret}")
    private String jwtSecret; // Base64로 인코딩된 Secret Key

    @Value("${app.auth.access-token-expiration-msec}")
    private long jwtExpirationInMs;

    @Value("${app.auth.refresh-token-expiration-msec}")
    private long jwtRefreshExpirationInMs;

    private SecretKey getSigningKey() {
        // Secret Key를 바이트 배열로 변환하고 HMAC SHA-256 키 생성
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Map<String, Object> claims, String email) {
        log.info("Generating JWT with subject (email): {}", email);
        log.info("Using SHA-256 for signing");
        return Jwts.builder()
                .setClaims(claims) // 추가적인 payload
                .setSubject(email) // 이메일을 sub에 설정
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Secret Key와 알고리즘 설정
                .compact();
    }

    public Claims getAllClaimsFromToken(String token) {
        try {
            log.info("Parsing JWT with SHA-256 and provided key");
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 검증 시 동일한 Secret Key 사용
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to parse JWT: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String email = getUsernameFromJWT(token);
            boolean isValid = email.equals(userDetails.getUsername()) && !isTokenExpired(token);
            log.info("Token validation result for [{}]: {}", email, isValid);
            return isValid;
        } catch (Exception ex) {
            log.warn("Token validation failed: {}", ex.getMessage());
            return false;
        }
    }

    public String getUsernameFromJWT(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String email = claims.getSubject();
            log.info("Extracted email [{}] from JWT", email);
            return email;
        } catch (Exception ex) {
            log.error("Failed to extract email from JWT: {}", ex.getMessage());
            throw ex;
        }
    }

    public String generateRefreshToken(Map<String, Object> claims, String subject) {
        log.info("Generating new JWT for subject: {}", subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationInMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    public Boolean validateRefreshToken(String token) {
        try {
            // Refresh Token의 유효성을 검사
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // Secret Key로 서명 확인
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expirationDate = claims.getExpiration();
            if (expirationDate.before(new Date())) {
                log.warn("Refresh Token has expired.");
                return false;
            }
            return true;
        } catch (Exception ex) {
            log.error("Invalid Refresh Token: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Refresh Token에서 사용자 이메일(또는 식별 정보) 추출
     * @param token JWT Refresh Token
     * @return 사용자 이메일(또는 식별 정보)
     */
    public String getUsernameFromRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // Secret Key로 서명 확인
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject(); // Subject에서 이메일 추출
            log.info("Extracted email [{}] from Refresh Token", email);
            return email;
        } catch (Exception ex) {
            log.error("Failed to extract email from Refresh Token: {}", ex.getMessage());
            throw ex; // 필요에 따라 커스텀 예외로 처리 가능
        }
    }
}

