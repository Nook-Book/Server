package com.nookbook.domain.challenge.domain;

import com.nookbook.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

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
    private LocalDateTime startDate;

    @Column(name="end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name="day_goal") // null 가능
    private int dailyGoal;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants;

    @Builder
    public Challenge(String title, String challengeCover, LocalDateTime startDate, LocalDateTime endDate, int dailyGoal) {
        this.title = title;
        this.challengeCover = challengeCover;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyGoal = dailyGoal;
    }
}
