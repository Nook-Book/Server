package com.nookbook.domain.challenge.domain.repository;

import com.nookbook.domain.challenge.domain.Challenge;
import com.nookbook.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("SELECT c FROM Challenge c JOIN c.participants p WHERE p.user = :user")
    List<Challenge> findAllByUserParticipant(@Param("user") User user);
}