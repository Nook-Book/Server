package com.nookbook.domain.user.application;

import com.nookbook.domain.user.domain.Friend;
import com.nookbook.domain.user.domain.FriendRequestStatus;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.FriendRepository;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user.dto.response.FriendsRequestRes;
import com.nookbook.domain.user.dto.response.SearchUserRes;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    // 검색
    public ResponseEntity<?> searchUsers(UserPrincipal userPrincipal, boolean isFriend, String keyword) {
        // User user = validUserByUserId(userPrincipal.getId());
        User user = validUserByUserId(1L);
        List<User> findUsers = isFriend ? getFriendsByKeyword(user, keyword) : getAllUsersByKeyword(user, keyword);
        List<SearchUserRes> searchUserRes = findUsers.stream()
                .map(findUser -> SearchUserRes.builder()
                        .userId(findUser.getUserId())
                        .nickname(findUser.getNickname())
                        .imageUrl(findUser.getImageUrl())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(searchUserRes)
                .build());
    }

    private List<User> getAllUsersByKeyword(User user, String keyword) {
        return userRepository.findUsersNotInFriendByKeyword(user, keyword);
    }

    private List<User> getFriendsByKeyword(User user, String keyword) {
        return keyword != null ?
                userRepository.findUsersInFriendByKeyword(user, keyword, FriendRequestStatus.FRIEND_ACCEPT)
                : userRepository.findUsersInFriend(user, FriendRequestStatus.FRIEND_ACCEPT);
    }

    @Transactional
    public ResponseEntity<?> sendFriendRequest(UserPrincipal userPrincipal, Long userId) {
        // User user = validUserByUserId(userPrincipal.getId());
        User user = validUserByUserId(1L);
        User targetUser = validUserByUserId(userId);
        Friend friend = Friend.builder()
                .sender(user)
                .receiver(targetUser)
                .build();
        friendRepository.save(friend);
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information("친구 신청이 완료되었습니다.")
                .build());
    }

    public ResponseEntity<?> getFriendRequestList(UserPrincipal userPrincipal) {
        // User user = validUserByUserId(userPrincipal.getId());
        User user = validUserByUserId(1L);
        List<Friend> sentRequests = friendRepository.findBySenderAndFriendRequestStatus(user, FriendRequestStatus.FRIEND_REQUEST);
        List<Friend> receivedRequests = friendRepository.findByReceiverAndFriendRequestStatus(user, FriendRequestStatus.FRIEND_REQUEST);
        FriendsRequestRes friendsRequestRes = FriendsRequestRes.builder()
                .sentRequest(buildSearchUserRes(sentRequests, true))
                .receivedRequest(buildSearchUserRes(receivedRequests, false))
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(friendsRequestRes)
                .build());
    }

    private List<SearchUserRes> buildSearchUserRes(List<Friend> friends, boolean isSent) {
        return friends.stream()
                .map(friend -> {
                    User user = isSent ? friend.getReceiver() : friend.getSender();
                    return SearchUserRes.builder()
                            .userId(user.getUserId())
                            .nickname(user.getNickname())
                            .imageUrl(user.getImageUrl())
                            .build();}
                )
                .collect(Collectors.toList());
    }

    // 보낸 요청 취소

    // 친구 요청 삭제
    // 친구 요청 수락

    private User validUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        DefaultAssert.isTrue(user.isPresent(), "사용자가 존재하지 않습니다.");
        return user.get();
    }
}
