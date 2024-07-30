package com.nookbook.domain.user.application;

import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user.dto.request.NicknameIdCheckReq;
import com.nookbook.domain.user.dto.request.NicknameCheckReq;
import com.nookbook.domain.user.dto.request.UserInfoReq;
import com.nookbook.domain.user.dto.response.NicknameCheckRes;
import com.nookbook.domain.user.dto.response.NicknameIdCheckRes;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import com.nookbook.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    final UserRepository userRepository;

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

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


    public ResponseEntity<?> checkNicknameId(UserPrincipal userPrincipal, NicknameIdCheckReq nicknameIdCheckReq) {
        userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다. 로그인 정보를 확인해주세요."));

        boolean isUnique = checkDuplicateNicknameId(nicknameIdCheckReq.getNicknameId());

        NicknameIdCheckRes nicknameIdCheckRes = NicknameIdCheckRes.builder()
                .isUnique(isUnique)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(nicknameIdCheckRes)
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    public ResponseEntity<?> checkNickname(UserPrincipal userPrincipal, NicknameCheckReq nicknameCheckReq) {
        userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

       boolean isUnique = checkDuplicateNickname(nicknameCheckReq.getNickname());

       NicknameCheckRes nicknameCheckRes = NicknameCheckRes.builder()
                .isUnique(isUnique)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(nicknameCheckRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    // 아이디 수정
    @Transactional
    public ResponseEntity<?> updateNicknameId(@CurrentUser UserPrincipal userPrincipal, Long userId, NicknameIdCheckReq nicknameIdCheckReq) {
        User user = validUserByUserId(userPrincipal.getId());
        String nicknameId = nicknameIdCheckReq.getNicknameId();
        boolean isAvailable = checkDuplicateNicknameId(nicknameId);

        DefaultAssert.isTrue(isAvailable, "이미 사용중인 아이디입니다.");

        user.updateNicknameId(nicknameId);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("아이디가 변경되었습니다.").build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 닉네임 수정
    @Transactional
    public ResponseEntity<?> updateNickname(@CurrentUser UserPrincipal userPrincipal, Long userId, NicknameCheckReq nicknameCheckReq) {
        User user = validUserByUserId(userPrincipal.getId());
        String nickname = nicknameCheckReq.getNickname();
        boolean isAvailable = checkDuplicateNickname(nickname);

        DefaultAssert.isTrue(isAvailable, "이미 사용중인 닉네임입니다.");

        user.updateNicknameId(nickname);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("닉네임이 변경되었습니다.").build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private boolean checkDuplicateNicknameId(String nicknameId) {
        return !userRepository.existsByNicknameId(nicknameId);
    }

    private boolean checkDuplicateNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    // 내 정보 조회


    // 프로필 사진 등록
    // 기록 전체 보기
    // 독서 리포트 조회


    private User validUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        DefaultAssert.isTrue(user.isPresent(), "사용자가 존재하지 않습니다.");
        return user.get();
    }
}
