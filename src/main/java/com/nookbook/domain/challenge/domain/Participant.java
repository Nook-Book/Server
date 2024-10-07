package com.nookbook.domain.challenge.domain;

import com.nookbook.domain.common.BaseEntity;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.challenge.domain.ParticipantStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Participant")
@NoArgsConstructor
@Getter
public class Participant extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ParticipantStatus participantStatus;

    @Builder
    public Participant(Challenge challenge, User user, ParticipantStatus participantStatus) {
        this.challenge = challenge;
        this.user = user;
        this.participantStatus = participantStatus;
    }
}
