package com.nookbook.domain.challenge.domain;

import com.nookbook.domain.common.BaseEntity;
import com.nookbook.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import java.time.LocalTime;
import java.util.ArrayList;
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

    @Column(name="day_goal", nullable = true) // null 가능
    private Integer dailyGoal; // 선택적 필드이므로 Integer 타입

    @Column(name="start_time", nullable = true)
    private LocalTime startTime;

    @Column(name="end_time", nullable = true)
    private LocalTime endTime;


    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;


    @Builder
    public Challenge(String title, String challengeCover, LocalDate startDate, LocalDate endDate, Integer dailyGoal, LocalTime startTime, LocalTime endTime, ChallengeStatus challengeStatus, List<Participant> participants, User owner) {
        this.title = title;
        this.challengeCover = challengeCover;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyGoal = dailyGoal;
        this.startTime = startTime;
        this.endTime = endTime;
        this.challengeStatus = challengeStatus;
        this.participants = new ArrayList<>();
        this.owner = owner;
    }

    public void updateChallengeCover(String coverImageUrl) {
        this.challengeCover = coverImageUrl;
    }

    public void updateChallengeInfo(String title, LocalDate startDate, LocalDate endDate, Integer dailyGoal, LocalTime startTime, LocalTime endTime) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyGoal = dailyGoal;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void changeOwner(Participant newOwner) {
        this.owner = newOwner.getUser();
    }
}
