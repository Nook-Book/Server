package com.nookbook.domain.challenge.application;

import com.nookbook.domain.challenge.domain.Challenge;
import com.nookbook.domain.challenge.domain.Invitation;
import com.nookbook.domain.challenge.domain.repository.InvitationRepository;
import com.nookbook.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {
    private final InvitationRepository invitationRepository;

    public void inviteParticipant(Challenge challenge, User participant) {
        Invitation invitation = Invitation.builder()
                .challenge(challenge)
                .user(participant)
                .build();

        invitationRepository.save(invitation);
    }

}
