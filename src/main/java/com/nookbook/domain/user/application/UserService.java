package com.nookbook.domain.user.application;

import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user.dto.request.NicknameIdReq;
import com.nookbook.domain.user.dto.request.NicknameReq;
import com.nookbook.domain.user.dto.request.UserInfoReq;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ResponseEntity<?> checkNicknameId(UserPrincipal userPrincipal, NicknameIdReq nicknameIdReq) {
        userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다. 로그인 정보를 확인해주세요."));

        if (userRepository.existsByNicknameId(nicknameIdReq.getNicknameId())) {
            return ResponseEntity.ok(ApiResponse.builder()
                    .check(false)
                    .information("이미 사용중인 아이디입니다.")
                    .build());
        } else {
            return ResponseEntity.ok(ApiResponse.builder()
                    .check(true)
                    .information("사용 가능한 아이디입니다.")
                    .build());
        }


    }

    public ResponseEntity<?> checkNickname(UserPrincipal userPrincipal, NicknameReq nicknameReq) {
        userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        if (userRepository.existsByNickname(nicknameReq.getNickname())) {
            return ResponseEntity.ok(ApiResponse.builder()
                    .check(false)
                    .information("이미 사용중인 닉네임입니다.")
                    .build());
        } else {
            return ResponseEntity.ok(ApiResponse.builder()
                    .check(true)
                    .information("사용 가능한 닉네임입니다.")
                    .build());
        }
    }
}
