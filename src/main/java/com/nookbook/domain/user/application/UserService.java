package com.nookbook.domain.user.application;

import com.nookbook.domain.collection.domain.Collection;
import com.nookbook.domain.collection.domain.CollectionStatus;
import com.nookbook.domain.collection.domain.repository.CollectionRepository;
import com.nookbook.domain.user.domain.Friend;
import com.nookbook.domain.user.domain.FriendRequestStatus;
import com.nookbook.domain.user.domain.repository.FriendRepository;
import com.nookbook.domain.user.dto.response.UserExistsRes;
import com.nookbook.infrastructure.s3.S3Uploader;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user.dto.request.NicknameIdCheckReq;
import com.nookbook.domain.user.dto.request.NicknameCheckReq;
import com.nookbook.domain.user.dto.request.UserInfoReq;
import com.nookbook.domain.user.dto.response.UserInfoRes;
import com.nookbook.domain.user.dto.response.NicknameCheckRes;
import com.nookbook.domain.user.dto.response.NicknameIdCheckRes;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import com.nookbook.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final CollectionRepository collectionRepository;
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
        createDefaultCollection(user);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("유저 ID, 닉네임 정보 등록 성공")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public void createDefaultCollection(User user) {
        List<String> titles = Arrays.asList("읽고 싶은", "읽는 중", "읽음");

        List<Collection> collections = titles.stream()
                .map(title -> Collection.builder()
                        .title(title)
                        .user(user)
                        .collectionStatus(CollectionStatus.MAIN)
                        .build())
                .collect(Collectors.toList());

        collectionRepository.saveAll(collections);
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
        User user = validUserByUserId(userPrincipal.getId());
        User targetUser = userId != null ? validUserByUserId(userId) : user;

        int num = friendRepository.countBySenderOrReceiverAndFriendRequestStatus(targetUser, FriendRequestStatus.FRIEND_ACCEPT);
        String friendRequestStatus = user != targetUser ? determineFriendStatus(user, targetUser) : null;
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
                return friend.getStatus().toString();
            }
        }).orElse("NONE");
    }

    @Transactional
    public ResponseEntity<?> updateImage(UserPrincipal userPrincipal, Boolean isDefaultImage, Optional<MultipartFile> image) {
        User user = validUserByUserId(userPrincipal.getId());
        if (!Objects.equals(user.getImageName(), "default.png")) {
            s3Uploader.deleteFile(user.getImageName());
        }
        String imageName;
        String imageUrl;
        if (isDefaultImage && image.isEmpty()) {
            imageName = "default.png";
            imageUrl = "https://nookbook-image-bucket.s3.amazonaws.com/default.png";
        } else {
            imageName = s3Uploader.uploadImage(image.get());
            imageUrl = s3Uploader.getFullPath(imageName);
        }
        user.updateImage(imageName, imageUrl);
        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("프로필 이미지가 변경되었습니다.").build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> checkUserExists(UserPrincipal userPrincipal) {
        // UserPrincipal이 null이면 회원가입이 안된 상태
        boolean isRegistered = userPrincipal != null;

        UserExistsRes userExistsRes = UserExistsRes.builder()
                .isRegistered(isRegistered)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(userExistsRes)
                .build();

        return ResponseEntity.ok(apiResponse);
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
