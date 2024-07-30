package com.nookbook.domain.user.application;

import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user.dto.request.NicknameIdCheckReq;
import com.nookbook.domain.user.dto.request.NicknameCheckReq;
import com.nookbook.domain.user.dto.request.UserInfoReq;
import com.nookbook.domain.user.dto.response.NicknameCheckRes;
import com.nookbook.domain.user.dto.response.NicknameIdCheckRes;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public ResponseEntity<?> saveUserInfo(UserPrincipal userPrincipal, UserInfoReq userInfoReq) {
        User user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        user.saveUserInfo(userInfoReq.getNicknameId(), userInfoReq.getNickname());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("유저 ID, 닉네임 정보 등록 성공")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @Transactional(readOnly = true)
    public ResponseEntity<?> checkNicknameId(UserPrincipal userPrincipal, NicknameIdCheckReq nicknameIdCheckReq) {
        userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다. 로그인 정보를 확인해주세요."));

        boolean isUnique = userRepository.existsByNicknameId(nicknameIdCheckReq.getNicknameId());

        NicknameIdCheckRes nicknameIdCheckRes = NicknameIdCheckRes.builder()
                .isUnique(isUnique)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(nicknameIdCheckRes)
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> checkNickname(UserPrincipal userPrincipal, NicknameCheckReq nicknameCheckReq) {
        userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        boolean isUnique = userRepository.existsByNickname(nicknameCheckReq.getNickname());

        NicknameCheckRes nicknameCheckRes = NicknameCheckRes.builder()
                .isUnique(isUnique)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(nicknameCheckRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
