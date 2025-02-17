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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<?> searchUsers(UserPrincipal userPrincipal, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        User user = validUserByUserId(1L);
        Page<SearchUserRes> searchUserRes = getAllUsersByKeyword(user, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(searchUserRes)
                .build());
    }

    public ResponseEntity<?> getFriends(UserPrincipal userPrincipal, String keyword) {
        User user = validUserByUserId(1L);
        List<SearchUserRes> searchUserRes = getFriendsByKeyword(user, keyword);
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

    private List<SearchUserRes> getFriendsByKeyword(User user, String keyword) {
        List<Friend> findFriends = (keyword != null)
                ? friendRepository.findBySenderOrReceiverAndStatusAndNicknameLikeKeyword(user, keyword, FriendRequestStatus.FRIEND_ACCEPT)
                : friendRepository.findBySenderOrReceiverAndStatus(user, FriendRequestStatus.FRIEND_ACCEPT);
        return buildSearchUserRes(findFriends, user);
    }

    private User getTargetUser(User user, Friend friend) {
        return user.equals(friend.getSender()) ? friend.getReceiver() : friend.getSender();
    }

    @Transactional
    public ResponseEntity<?> sendFriendRequest(UserPrincipal userPrincipal, Long userId) {
        // User user = validUserByUserId(userPrincipal.getId());
        User user = validUserByUserId(1L);
        User targetUser = validUserByUserId(userId);
        DefaultAssert.isTrue(targetUser != user, "본인에게 친구 요청을 보낼 수 없습니다.");

        boolean available = !friendRepository.existsBySenderAndReceiver(user, targetUser) || !friendRepository.existsByReceiverAndSender(targetUser, user);
        DefaultAssert.isTrue(available, "이미 친구 요청을 보내거나 받은 상태입니다.");

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
                .sentRequest(buildSearchUserRes(sentRequests, user))
                .receivedRequest(buildSearchUserRes(receivedRequests, user))
                .build();
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(friendsRequestRes)
                .build());
    }

    private List<SearchUserRes> buildSearchUserRes(List<Friend> friends, User user) {
        return friends.stream()
                .map(friend -> {
                    User targetUser = getTargetUser(user, friend);
                    return SearchUserRes.builder()
                            .userId(targetUser.getUserId())
                            .friendId(friend.getFriendId())
                            .nickname(targetUser.getNickname())
                            .imageUrl(targetUser.getImageUrl())
                            .build();}
                )
                .collect(Collectors.toList());
    }

    // 보낸 요청 취소
    @Transactional
    public ResponseEntity<?> deleteFriendRequest(UserPrincipal userPrincipal, Long friendId, boolean isFriendAccept) {
        // User user = validUserByUserId(userPrincipal.getId());
        User user = validUserByUserId(1L);
        Friend friend = validFriendByFriendId(friendId);
        String msg;
        if (!isFriendAccept) {
            DefaultAssert.isTrue(friend.getSender() == user, "내가 보낸 친구 요청이 아닙니다.");
            msg = "친구 요청이 취소되었습니다.";
        } else {
            msg = "친구가 삭제되었습니다.";
        }
        friendRepository.delete(friend);
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(msg)
                .build());
    }

    // 친구 요청 삭제/수락
    @Transactional
    public ResponseEntity<?> updateFriendRequestStatus(UserPrincipal userPrincipal, Long friendId, boolean isAccept) {
        // User user = validUserByUserId(userPrincipal.getId());
        User user = validUserByUserId(1L);
        Friend friend = validFriendByFriendId(friendId);
        DefaultAssert.isTrue(friend.getReceiver() == user, "내가 받은 친구 요청이 아닙니다.");

        String msg;
        if (isAccept) {
            friend.updateFriendRequestStatus(FriendRequestStatus.FRIEND_ACCEPT);
            msg = "친구 요청을 수락했습니다.";
        } else {
            friendRepository.delete(friend);
            msg = "친구 요청을 거절했습니다.";
        }
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(msg)
                .build());
    }


    // 사용자의 친구 목록 조회
    public List<Friend> getFriends(User user) {
        // User user = validUserByUserId(userPrincipal.getId());
        return friendRepository.findAcceptedFriends(user);
    }

    private User validUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        DefaultAssert.isTrue(user.isPresent(), "사용자가 존재하지 않습니다.");
        return user.get();
    }

    private Friend validFriendByFriendId(Long friendId) {
        Optional<Friend> friendOptional = friendRepository.findById(friendId);
        DefaultAssert.isTrue(friendOptional.isPresent(), "친구 요청이 존재하지 않습니다.");
        return friendOptional.get();
    }
}
