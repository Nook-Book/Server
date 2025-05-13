package com.nookbook.domain.user.application;

import com.nookbook.domain.user.domain.Friend;
import com.nookbook.domain.user.domain.FriendRequestStatus;
import com.nookbook.domain.user.domain.repository.FriendRepository;
import com.nookbook.domain.user.dto.request.ExpoPushTokenReq;
import com.nookbook.domain.user.dto.response.*;
import com.nookbook.infrastructure.s3.S3Uploader;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user.dto.request.NicknameIdCheckReq;
import com.nookbook.domain.user.dto.request.NicknameCheckReq;
import com.nookbook.domain.user.dto.request.UserInfoReq;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import com.nookbook.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final FriendRepository friendRepository;

    @Transactional
    public void saveUser(User user) {
        // 유저 정보 저장
        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public ResponseEntity<?> saveUserInfo(UserPrincipal userPrincipal, UserInfoReq userInfoReq) {
        User user = validUserByUserId(userPrincipal.getId());
        user.saveUserInfo(userInfoReq.getNicknameId(), userInfoReq.getNickname());


        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("유저 ID, 닉네임 정보 등록 성공")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    public ResponseEntity<?> checkNicknameId(UserPrincipal userPrincipal, NicknameIdCheckReq nicknameIdCheckReq) {
        validUserByUserId(userPrincipal.getId());
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
        validUserByUserId(userPrincipal.getId());
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

    @Transactional
    public ResponseEntity<?> updateNicknameId(UserPrincipal userPrincipal, NicknameIdCheckReq nicknameIdCheckReq) {
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

    @Transactional
    public ResponseEntity<?> updateNickname(UserPrincipal userPrincipal, NicknameCheckReq nicknameCheckReq) {
        User user = validUserByUserId(userPrincipal.getId());
        String nickname = nicknameCheckReq.getNickname();
        boolean isAvailable = checkDuplicateNickname(nickname);

        DefaultAssert.isTrue(isAvailable, "이미 사용중인 닉네임입니다.");
        user.updateNickname(nickname);
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

    public ResponseEntity<ApiResponse> getUserInfo(UserPrincipal userPrincipal, Long userId) {
        boolean isSame = userId == userPrincipal.getId();
        User user = validUserByUserId(userPrincipal.getId());
        User targetUser = isSame ? user : validUserByUserId(userId);

        int num = friendRepository.countBySenderOrReceiverAndFriendRequestStatus(targetUser, FriendRequestStatus.FRIEND_ACCEPT);
        String friendRequestStatus = isSame ? null : determineFriendStatus(user, targetUser);
        UserInfoRes userInfoRes = UserInfoRes.builder()
                .nicknameId(targetUser.getNicknameId())
                .nickname(targetUser.getNickname())
                .imageUrl(targetUser.getImageUrl())
                .friendsNum(num)
                .requestStatus(friendRequestStatus)
                .build();
        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(userInfoRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private String determineFriendStatus(User user, User targetUser) {
        Optional<Friend> friendOptional = friendRepository.findBySenderAndReceiver(user, targetUser)
                .or(() -> friendRepository.findBySenderAndReceiver(targetUser, user));
        return friendOptional.map(friend -> {
            if (friend.getFriendRequestStatus() == FriendRequestStatus.FRIEND_REQUEST) {
                return user.equals(friend.getSender()) ? "REQUEST_SENT" : "REQUEST_RECEIVED";
            } else {
                return friend.getFriendRequestStatus().toString();
            }
        }).orElse("NONE");
    }

    @Transactional
    public ResponseEntity<?> updateImage(UserPrincipal userPrincipal, Boolean isDefaultImage, MultipartFile image) {
        User user = validUserByUserId(userPrincipal.getId());
        if (!Objects.equals(user.getImageName(), "default.png")) {
            s3Uploader.deleteFile(user.getImageName());
        }
        String imageName;
        String imageUrl;
        if (isDefaultImage && image==null) {
            imageUrl = "https://nookbook-image-bucket.s3.amazonaws.com/default.png";
            imageName = "default.png";
        } else {
            imageUrl = s3Uploader.uploadImage(image);
            imageName = s3Uploader.extractFileName(imageUrl);
        }
        user.updateImage(imageName, imageUrl);
        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("프로필 이미지가 변경되었습니다.").build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> checkUserExists(UserPrincipal userPrincipal) {
        boolean isRegistered;
        // UserPrincipal이 null이면 회원가입이 안된 상태
        if (userPrincipal == null) {
            log.warn("UserPrincipal is null. Returning false.");
            return ResponseEntity.ok(ApiResponse.builder()
                    .check(true)
                    .information(UserExistsRes.builder().isRegistered(false).build())
                    .build());
        }

        try {
            validUserByUserId(userPrincipal.getId());
            isRegistered = true;
        } catch (UsernameNotFoundException e) {
            log.warn("User not found: {}", userPrincipal.getId());
            isRegistered = false;
        } catch (Exception e) {  // 다른 예외도 잡도록 추가
            log.error("Unexpected error during user validation: {}", e.getMessage());
            isRegistered = false;
        }

        UserExistsRes userExistsRes = UserExistsRes.builder()
                .isRegistered(isRegistered)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(userExistsRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    //saveExpoPushToken
    @Transactional
    public ResponseEntity<?> saveExpoPushToken(UserPrincipal userPrincipal, ExpoPushTokenReq expoPushToken) {
        User user = validUserByUserId(userPrincipal.getId());
        user.updateExpoPushToken(expoPushToken.getExpoPushToken());
        // 푸시 토큰 저장 성공 메시지
        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("Expo push token 저장 성공").build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> searchUsers(UserPrincipal userPrincipal, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        User user = validUserByUserId(userPrincipal.getId());
        Page<SearchUserRes> searchUserRes = getAllUsersByKeyword(user, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(searchUserRes)
                .build());
    }

    private Page<SearchUserRes> getAllUsersByKeyword(User user, String keyword, Pageable pageable) {
        Page<User> findUsers = userRepository.findUsersNotInFriendByKeyword(user, keyword, pageable);
        return findUsers.map(findUser -> SearchUserRes.builder()
                .userId(findUser.getUserId())
                .nickname(findUser.getNickname())
                .imageUrl(findUser.getImageUrl())
                .build());
    }

    private User validUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        DefaultAssert.isTrue(user.isPresent(), "사용자가 존재하지 않습니다.");
        return user.get();
    }

    public User findById(long l) {
        return userRepository.findById(l).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

}
