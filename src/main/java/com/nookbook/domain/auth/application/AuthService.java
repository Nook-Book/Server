package com.nookbook.domain.auth.application;

import com.nookbook.domain.auth.domain.Token;
import com.nookbook.domain.auth.domain.repository.TokenRepository;
import com.nookbook.domain.auth.dto.request.FindNicknameIdReq;
import com.nookbook.domain.auth.dto.request.ResetPasswordReq;
import com.nookbook.domain.auth.dto.response.LoginResponse;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.Provider;
import com.nookbook.domain.user.domain.Role;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user.exception.OAuthUserPasswordResetException;
import com.nookbook.domain.user.exception.UserNotFoundException;
import com.nookbook.domain.verification.application.VerificationService;
import com.nookbook.domain.verification.exception.VerificationNotCompletedException;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.config.security.util.JwtTokenUtil;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;


    public String verifyIdTokenAndExtractUsername(String idToken, String email) {
        if (idToken == null || idToken.trim().isEmpty()) {
            throw new IllegalArgumentException("ID 토큰은 null이거나 비어 있을 수 없습니다");
        }
        return idTokenVerifier.verifyIdToken(idToken, email);
    }


    @Transactional
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

    @Transactional
    public ResponseEntity<?> logout(UserPrincipal userPrincipal) {

        // 사용자 검증
        User user = validateUser(userPrincipal);
        String email = user.getEmail();

        // 토큰 정보 삭제
        deleteToken(email);

        // expoToken 삭제 (null로 update)
        user.updateExpoPushToken(null);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("로그아웃 성공")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> exit(UserPrincipal userPrincipal) {
        User user = validateUser(userPrincipal);
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

    @Transactional
    public ResponseEntity<?> signUp(String email, String password, String nickname) {
        // 이메일 중복 체크
        Optional<User> existingUser = userRepository.findByEmail(email);
        DefaultAssert.isTrue(existingUser.isEmpty(), "이미 사용 중인 이메일입니다.");

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 생성
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .provider(Provider.local)
                .providerId(email)
                .role(Role.USER)
                .build();

        userRepository.save(user);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("회원가입이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> localLogin(String email, String password) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // Provider 검증 (local 계정인지 확인)
        DefaultAssert.isTrue(user.getProvider() == Provider.local, "소셜 로그인 계정입니다. 소셜 로그인을 이용해주세요.");

        // 비밀번호 검증
        DefaultAssert.isTrue(passwordEncoder.matches(password, user.getPassword()), "이메일 또는 비밀번호가 올바르지 않습니다.");

        // JWT 토큰 생성
        String accessToken = jwtTokenUtil.generateToken(new HashMap<>(), email);
        String refreshToken = jwtTokenUtil.generateRefreshToken(new HashMap<>(), email);

        // Refresh token DB에 저장 (기존 토큰이 있으면 업데이트)
        Token existingToken = tokenRepository.findByEmail(email);
        if (existingToken != null) {
            tokenRepository.delete(existingToken);
        }

        Token tokenEntity = Token.builder()
                .email(email)
                .refreshToken(refreshToken)
                .build();
        tokenRepository.save(tokenEntity);

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(loginResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }



    private void deleteToken(String email) {
        Token token = tokenRepository.findByEmail(email);
        if (token != null) {
            tokenRepository.delete(token);
        }
    }

    public ResponseEntity<?> findNicknameId(FindNicknameIdReq findNicknameIdReq) {
        String email = findNicknameIdReq.getEmail();

        if (!verificationService.isVerified(email)) {
            throw new VerificationNotCompletedException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        verificationService.removeVerified(email);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(user.getNicknameId())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> resetPassword(ResetPasswordReq resetPasswordReq) {
        String email = resetPasswordReq.getEmail();

        if (!verificationService.isVerified(email)) {
            throw new VerificationNotCompletedException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (user.getProvider() != Provider.local) {
            throw new OAuthUserPasswordResetException();
        }

        String encodedPassword = passwordEncoder.encode(resetPasswordReq.getNewPassword());
        user.updatePassword(encodedPassword);

        verificationService.removeVerified(email);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("비밀번호가 성공적으로 변경되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 사용자 검증 메서드
    private User validateUser(UserPrincipal userPrincipal) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal.getUsername());
        if (userDetails instanceof UserPrincipal) {
            return userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(UserNotFoundException::new);
        } else {
            throw new UserNotFoundException();
        }
    }
}
