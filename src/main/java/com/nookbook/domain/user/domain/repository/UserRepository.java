package com.nookbook.domain.user.domain.repository;

import com.nookbook.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    Page<User> findUsersNotInFriendByKeyword(@Param("user") User user, @Param("keyword") String keyword, Pageable pageable);

}
