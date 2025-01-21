package com.nookbook.domain.friend.application;

import com.nookbook.domain.friend.domain.Friend;
import com.nookbook.domain.friend.domain.repository.FriendRepository;
import com.nookbook.domain.friend.dto.response.FriendListRes;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {
    private final UserService userService;
    private final FriendRepository friendRepository;

    public ResponseEntity<?> getFriendList(UserPrincipal userPrincipal) {
        User user = validateUser(userPrincipal);
        List<Friend> friends = user.getFriends();

        // 친구의 유저ID
        List<FriendListRes> friendList = friends.stream()
                .map(this::getUserInfoByFriend)
                .toList();

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information(friendList)
                .build();

        return ResponseEntity.ok(response);
    }

    private FriendListRes getUserInfoByFriend(Friend friend) {
        User user = friend.getUser();
        String nickname = user.getNickname();
        String profileImage = user.getImageUrl();
        return FriendListRes.builder()
                .userId(user.getUserId())
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }


    // 사용자 검증 메서드
    private User validateUser(UserPrincipal userPrincipal) {
//        return userService.findByEmail(userPrincipal.getEmail())
//                .orElseThrow(UserNotFoundException::new);
        // userId=1L로 고정
        return userService.findById(1L);
    }
}
