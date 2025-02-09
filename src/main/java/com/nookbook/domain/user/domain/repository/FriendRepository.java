package com.nookbook.domain.user.domain.repository;

import com.nookbook.domain.user.domain.Friend;
import com.nookbook.domain.user.domain.FriendRequestStatus;
import com.nookbook.domain.user.domain.User;
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
}
