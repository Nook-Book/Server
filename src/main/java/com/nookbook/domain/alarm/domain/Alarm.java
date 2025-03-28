package com.nookbook.domain.alarm.domain;

import com.nookbook.domain.common.BaseEntity;
import com.nookbook.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Alarm")
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id", nullable = false)
    private Long alarmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long senderId; // 알람을 보낸 사람

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType; // WAKE_UP, FRIEND_REQUEST, CHALLENGE_INVITE

    private String message;

    @Column(name = "target_id")
    private Long targetId; // 알람의 대상 ID 값입니다. 챌린지의 ID 또는 사용자의 ID입니다.

    public static Alarm create(User receiver, Long senderId, AlarmType alarmType, String message, Long targetId){
        return Alarm.builder()
                .user(receiver)
                .senderId(senderId)
                .alarmType(alarmType)
                .message(message)
                .targetId(targetId)
                .build();
    }

}
