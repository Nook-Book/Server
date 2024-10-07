package com.nookbook.domain.challenge.application;

import com.nookbook.domain.challenge.domain.Challenge;
import com.nookbook.domain.challenge.domain.Participant;
import com.nookbook.domain.challenge.domain.ParticipantStatus;
import com.nookbook.domain.challenge.domain.repository.ParticipantRepository;
import com.nookbook.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    public void saveParticipant(User user, Challenge challenge) {

        // 챌린지를 생성한 유저는 participant로 등록
        Participant participant = Participant.builder()
                .user(user)
                .challenge(challenge)
                .participantStatus(ParticipantStatus.RESTING)
                .build();

        participantRepository.save(participant);
    }

}
