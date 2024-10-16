package com.nookbook.domain.auth.application;

import com.nookbook.domain.auth.domain.Token;
import com.nookbook.domain.auth.domain.repository.TokenRepository;
import com.nookbook.domain.auth.dto.request.RefreshTokenReq;
import com.nookbook.domain.auth.dto.request.SignInReq;
import com.nookbook.domain.auth.dto.response.AuthRes;
import com.nookbook.domain.auth.dto.response.TokenMapping;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.exception.DefaultException;
import com.nookbook.global.payload.ApiResponse;
import com.nookbook.global.payload.ErrorCode;
import com.nookbook.domain.auth.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final CustomTokenProviderService customTokenProviderService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<?> signIn(SignInReq signInReq) {
        logger.debug("Sign in request received: {}", signInReq);

        Optional<User> optionalUser = userRepository.findByEmail(signInReq.getEmail());
        if (!optionalUser.isPresent()) {
            logger.error("유저 정보를 찾을 수 없습니다: {}", signInReq.getEmail());
            throw new DefaultException(ErrorCode.INVALID_CHECK, "유저 정보가 유효하지 않습니다.");
        }

        User user = optionalUser.get();
        logger.debug("Found user: {}", user);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            signInReq.getProviderId()  // providerId 사용
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);
            Token token = Token.builder()
                    .refreshToken(tokenMapping.getRefreshToken())
                    .email(tokenMapping.getEmail())
                    .build();
            tokenRepository.save(token);

            AuthRes authResponse = AuthRes.builder()
                    .accessToken(tokenMapping.getAccessToken())
                    .refreshToken(token.getRefreshToken())
                    .build();

            ApiResponse apiResponse = ApiResponse.builder()
                    .check(true)
                    .information(authResponse).build();

            logger.debug("Sign in response: {}", apiResponse);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage(), e);
            throw new DefaultException(ErrorCode.INVALID_CHECK, "유저 정보가 유효하지 않습니다.");
        }
    }

    @Transactional
    public ResponseEntity<?> refresh(RefreshTokenReq tokenRefreshRequest) {
        logger.debug("Refresh token request received: {}", tokenRefreshRequest);

        boolean checkValid = valid(tokenRefreshRequest.getRefreshToken());
        DefaultAssert.isAuthentication(checkValid);

        Token token = tokenRepository.findByRefreshToken(tokenRefreshRequest.getRefreshToken())
                .orElseThrow(InvalidTokenException::new);
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.getEmail());

        TokenMapping tokenMapping;
        Long expirationTime = customTokenProviderService.getExpiration(tokenRefreshRequest.getRefreshToken());
        if (expirationTime > 0) {
            tokenMapping = customTokenProviderService.refreshToken(authentication, token.getRefreshToken());
        } else {
            tokenMapping = customTokenProviderService.createToken(authentication);
        }

        Token updateToken = token.updateRefreshToken(tokenMapping.getRefreshToken());

        AuthRes authResponse = AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(updateToken.getRefreshToken())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(authResponse)
                .build();

        logger.debug("Refresh token response: {}", apiResponse);
        return ResponseEntity.ok(apiResponse);
    }

    private boolean valid(String refreshToken) {
        boolean validateCheck = customTokenProviderService.validateToken(refreshToken);
        DefaultAssert.isTrue(validateCheck, "Token 검증에 실패하였습니다.");

        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);
        DefaultAssert.isTrue(token.isPresent(), "탈퇴 처리된 회원입니다.");

        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(refreshToken);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        DefaultAssert.isTrue(token.get().getEmail().equals(userPrincipal.getEmail()), "사용자 인증에 실패하였습니다.");

        return true;
    }
}