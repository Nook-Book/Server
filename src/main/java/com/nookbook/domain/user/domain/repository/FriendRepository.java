package com.nookbook.domain.user.domain.repository;

import com.nookbook.domain.user.domain.Friend;
import com.nookbook.domain.user.domain.FriendRequestStatus;
import com.nookbook.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findBySenderAndReceiver(User user, User targetUser);
    @Query("SELECT COUNT(f) " +
            "FROM Friend f " +
            "WHERE (f.sender = :user OR f.receiver = :user) AND f.friendRequestStatus = :friendRequestStatus")
    int countBySenderOrReceiverAndFriendRequestStatus(@Param("user") User user, @Param("friendRequestStatus") FriendRequestStatus friendRequestStatus);

    List<Friend> findBySenderAndFriendRequestStatus(User user, FriendRequestStatus friendRequestStatus);

    List<Friend> findByReceiverAndFriendRequestStatus(User user, FriendRequestStatus friendRequestStatus);

    boolean existsBySenderAndReceiver(User user, User targetUser);

    boolean existsByReceiverAndSender(User targetUser, User user);

    @Query("SELECT f FROM Friend f " +
            "WHERE (" +
            "    (f.sender = :user AND (f.receiver.nickname LIKE %:keyword%  OR f.receiver.nicknameId LIKE %:keyword% )) " +
            "    OR " +
            "    (f.receiver = :user AND (f.sender.nickname LIKE %:keyword% OR f.sender.nicknameId LIKE %:keyword%)) " +
            ") " +
            "AND f.friendRequestStatus = :friendRequestStatus")
    Page<Friend> findBySenderOrReceiverAndStatusAndNicknameLikeKeyword(
            @Param("user") User user,
            @Param("keyword") String keyword,
            @Param("friendRequestStatus") FriendRequestStatus friendRequestStatus,
            Pageable pageable);

    @Query("SELECT f FROM Friend f " +
            "WHERE (f.sender = :user OR f.receiver = :user) " +
            "AND f.friendRequestStatus = :friendRequestStatus")
    Page<Friend> findBySenderOrReceiverAndStatus(
            @Param("user") User user,
            @Param("friendRequestStatus") FriendRequestStatus friendRequestStatus,
            Pageable pageable);


    // reciever_id 또는 sender_id가 user이면서, friendRequestStatus가 FRIEND_ACCEPT인 친구 목록 조회
    @Query("SELECT f FROM Friend f " +
            "WHERE (f.sender = :user OR f.receiver = :user) " +
            "AND f.friendRequestStatus = 'FRIEND_ACCEPT'")
    Page<Friend> findAcceptedFriends(@Param("user") User user, Pageable pageable);
}
