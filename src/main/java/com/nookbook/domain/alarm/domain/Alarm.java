package com.nookbook.domain.alarm.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookbook.domain.alarm.message.AlarmMessageTemplate;
import com.nookbook.domain.common.BaseEntity;
import com.nookbook.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

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
    private User user; // receiver

    private Long senderId;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType; // 알림 타입(WAKE_UP, FRIEND, CHALLENGE)

    @Enumerated(EnumType.STRING)
    private AlarmMessageTemplate template; // 메시지 템플릿

    @Column(columnDefinition = "TEXT")
    private String argsJson; // JSON 문자열로 저장된 파라미터

    @Column(name = "target_id")
    private Long targetId; // 챌린지 ID 또는 사용자 ID

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false; // 읽음 여부

    // argsJson → Map<String, String>

    @Transient
    public Map<String, String> getArgs() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(argsJson, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of(); // 오류 시 빈 맵
        }
    }

    public static Alarm create(User receiver, Long senderId, AlarmType type,
                               AlarmMessageTemplate template, Map<String, String> args, Long targetId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return Alarm.builder()
                    .user(receiver)
                    .senderId(senderId)
                    .alarmType(type)
                    .template(template)
                    .argsJson(objectMapper.writeValueAsString(args))
                    .targetId(targetId)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("알림 파라미터 직렬화 실패", e);
        }
    }

    // 알림 읽음
    public void markAsRead() {
        this.isRead = true;
    }
}