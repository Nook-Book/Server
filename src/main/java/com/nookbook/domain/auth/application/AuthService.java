package com.nookbook.domain.auth.application;

import com.nookbook.domain.auth.domain.Token;
import com.nookbook.domain.auth.domain.repository.TokenRepository;
import com.nookbook.domain.auth.dto.response.LoginResponse;
import com.nookbook.domain.user.domain.Provider;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.config.security.util.JwtTokenUtil;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final IdTokenVerifier idTokenVerifier;
    private final UserDetailsService userDetailsService;

    private final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo";


    public String verifyIdTokenAndExtractUsername(String idToken, String email) {
        if (idToken == null || idToken.trim().isEmpty()) {
            throw new IllegalArgumentException("ID 토큰은 null이거나 비어 있을 수 없습니다");
        }
        return idTokenVerifier.verifyIdToken(idToken, email);
    }


    public ResponseEntity<?> loginWithIdToken(String idToken, String email) {
        String username = verifyIdTokenAndExtractUsername(idToken, email);
        if (username != null) {
            String accessToken = jwtTokenUtil.generateToken(new HashMap<>(), username);
            String refreshToken = jwtTokenUtil.generateRefreshToken(new HashMap<>(), username);

            // Refresh token을 DB에 저장
            Token tokenEntity = Token.builder()
                    .email(email)
                    .refreshToken(refreshToken)
                    .build();
            tokenRepository.save(tokenEntity);

            // 사용자 정보를 DB에 저장
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isEmpty()) {
                User user = User.builder()
                        .email(email)
                        .password(null)
                        .provider(Provider.kakao)
                        .build();
                userRepository.save(user);
            }


            LoginResponse loginResponse = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            ApiResponse apiResponse = ApiResponse.builder()
                    .check(true)
                    .information(loginResponse)
                    .build();

            return ResponseEntity.ok(apiResponse);

        } else {
            throw new RuntimeException("유효하지 않은 ID 토큰");
        }


    }
    public ResponseEntity<?> logout(UserPrincipal userPrincipal) {
        User user = (User) userDetailsService.loadUserByUsername(userPrincipal.getUsername());
        String email = user.getEmail();

        // 토큰 정보 삭제
        deleteToken(email);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("로그아웃 성공")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> exit(UserPrincipal userPrincipal) {
        User user = (User) userDetailsService.loadUserByUsername(userPrincipal.getUsername());
        String email = user.getEmail();

        // 사용자 토큰 정보 삭제
        deleteToken(email);

        // 사용자 정보 삭제
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User deleteUser = userOptional.get();
            userRepository.delete(deleteUser);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("회원 탈퇴 성공")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    private void deleteToken(String email) {
        Token token = tokenRepository.findByEmail(email);
        if (token != null) {
            tokenRepository.delete(token);
        }
    }

}
