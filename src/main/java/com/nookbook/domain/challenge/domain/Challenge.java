package com.nookbook.domain.challenge.domain;

import com.nookbook.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import java.util.List;

@Entity
@Table(name="Challenge")
@NoArgsConstructor
@Getter
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challege_id", nullable = false)
    private Long challengeId;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="challenge_cover", nullable = false)
    private String challengeCover;

    @Column(name="start_date", nullable = false)
    private LocalDate startDate;

    @Column(name="end_date", nullable = false)
    private LocalDate endDate;

    @Column(name="day_goal") // null 가능
    private int dailyGoal;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants;

    @Builder
    public Challenge(String title, String challengeCover, LocalDate startDate, LocalDate endDate, int dailyGoal, ChallengeStatus challengeStatus) {
        this.title = title;
        this.challengeCover = challengeCover;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyGoal = dailyGoal;
        this.challengeStatus = challengeStatus;
    }
}
