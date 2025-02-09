package com.nookbook.domain.challenge.domain.repository;

import com.nookbook.domain.challenge.domain.Challenge;
import com.nookbook.domain.challenge.domain.Invitation;
import com.nookbook.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findAllByChallenge(Challenge challenge);

    List<Invitation> findAllByUser(User user);
}
