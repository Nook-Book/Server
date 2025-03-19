package com.nookbook.domain.auth.application;

import com.nookbook.domain.auth.dto.response.TokenMapping;
import com.nookbook.global.config.security.OAuth2Config;
import com.nookbook.global.config.security.token.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomTokenProviderService {

    private final OAuth2Config oAuth2Config;
    private final CustomUserDetailsService customUserDetailsService;

    public TokenMapping refreshToken(Authentication authentication, String refreshToken) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();

        Date accessTokenExpiresIn = new Date(now.getTime() + oAuth2Config.getAuth().getAccessTokenExpirationMsec());

        String secretKey = oAuth2Config.getAuth().getTokenSecret();
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));


        String accessToken = Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenMapping.builder()
                .email(userPrincipal.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenMapping createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();

        Date accessTokenExpiresIn = new Date(now.getTime() + oAuth2Config.getAuth().getAccessTokenExpirationMsec());
        Date refreshTokenExpiresIn = new Date(now.getTime() + oAuth2Config.getAuth().getRefreshTokenExpirationMsec());

        String base64SecretKey = oAuth2Config.getAuth().getTokenSecret();
        byte[] keyBytes = Decoders.BASE64.decode(base64SecretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String accessToken = Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenMapping.builder()
                .email(userPrincipal.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String getEmailFromToken(String token) {
        log.debug("Extracting email from token: {}", token);  // 추가된 로깅
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(oAuth2Config.getAuth().getTokenSecret())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();
        log.debug("Email extracted: {}", email);  // 추가된 로깅
        return email;
    }

    public UsernamePasswordAuthenticationToken getAuthenticationByEmail(String token) {
        String email = getEmailFromToken(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Long getExpiration(String token) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(oAuth2Config.getAuth().getTokenSecret())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        // 시간 계산
        return (expiration.getTime() - now);
    }

    public boolean validateToken(String token) {
        try {
            log.info("Validating token: {}", token);

            String base64SecretKey = oAuth2Config.getAuth().getTokenSecret();
            byte[] keyBytes = Decoders.BASE64.decode(base64SecretKey);
            Key key = Keys.hmacShaKeyFor(keyBytes);

            log.info("Key: {}", key);

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            log.info("Token validated successfully!");
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException ex) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException ex) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException ex) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException ex) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
