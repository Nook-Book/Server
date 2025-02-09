package com.nookbook.domain.challenge.domain.repository;

import com.nookbook.domain.challenge.domain.Challenge;
import com.nookbook.domain.challenge.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findAllByChallenge(Challenge challenge);

    @Query("select p from Participant p where p.user.userId = :friendId and p.challenge = :challenge")
    boolean existsByUserAndChallenge(Long friendId, Challenge challenge);

    boolean existsByUserUserIdAndChallenge(Long friendId, Challenge challenge);

}
