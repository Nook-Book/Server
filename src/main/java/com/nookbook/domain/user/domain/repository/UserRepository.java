package com.nookbook.domain.user.domain.repository;

import com.nookbook.domain.user.domain.FriendRequestStatus;
import com.nookbook.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    Optional<User> findByEmail(String email);

    boolean existsByNicknameId(String nicknameId);

    boolean existsByNickname(String nickname);

    @Query("SELECT u FROM User u " +
            "WHERE u != :user " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM Friend f " +
            "    WHERE (f.sender = :user AND f.receiver = u) OR (f.receiver = :user AND f.sender = u)" +
            ") " +
            "AND (u.nicknameId LIKE %:keyword% OR u.nickname LIKE %:keyword%)")
    List<User> findUsersNotInFriendByKeyword(@Param("user") User user, @Param("keyword") String keyword);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE (u.nicknameId = :keyword OR u.nickname LIKE %:keyword%) " +
            "AND (u IN (SELECT f.receiver FROM Friend f WHERE f.sender = :user AND f.friendRequestStatus = :friendRequestStatus) " +
            "OR u IN (SELECT f.sender FROM Friend f WHERE f.receiver = :user AND f.friendRequestStatus = :friendRequestStatus))")
    List<User> findUsersInFriendByKeyword(@Param("user") User user, @Param("keyword") String keyword, @Param("friendRequestStatus") FriendRequestStatus friendRequestStatus);

    @Query("SELECT DISTINCT u " +
            "FROM User u " +
            "JOIN Friend f ON (f.sender = :user OR f.receiver = :user) " +
            "WHERE (f.sender = u OR f.receiver = u) " +
            "AND f.friendRequestStatus = :friendRequestStatus")
    List<User> findUsersInFriend(@Param("user") User user, @Param("friendRequestStatus") FriendRequestStatus friendRequestStatus);


}
