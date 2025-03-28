package com.nookbook.domain.alarm.dto.response;

import com.nookbook.domain.alarm.domain.Alarm;
import com.nookbook.domain.alarm.domain.AlarmType;
import com.nookbook.global.util.TimeFormatter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlarmRes {

    @Schema(type = "Long", example = "1", description = "알람의 ID 값입니다.")
    private Long alarmId;

    @Schema(type = "String", example = "기무라타쿠야님이 회원님의 친구 요청을 수락하였습니다.", description = "알람 내용입니다.")
    private String message;

    @Schema(
            type = "String",
            allowableValues = { "WAKE_UP", "FRIEND", "CHALLENGE" },
            example = "WAKE_UP", description = "알람의 타입입니다."
    )
    private AlarmType alarmType;

    @Schema(type = "Long", example = "1", description = "알람의 대상 ID 값입니다. 챌린지의 ID 또는 사용자의 ID입니다.")
    private Long targetId;

    @Schema(type = "String",
            allowableValues = { "HOUR", "DAY" },
            example = "HOUR", description = "알림 발생 시간의 타입입니다.")
    private String timeType;

    @Schema(type = "int", example = "13", description = "알림 발생 일시(시간 또는 일 단위)입니다. 시간 타입에 따라 다르게 표시됩니다.")
    private int timeValue;


    public static AlarmRes fromEntity(Alarm entity) {
        // 알림 발생 시간을 ~시간 전 / ~일 전으로 변환
        TimeFormatter.TimeResult result = TimeFormatter.formatToTimeAgo(entity.getCreatedAt());

        {
            return AlarmRes.builder()
                    .alarmId(entity.getAlarmId())  // 알람 ID
                    .message(entity.getMessage())  // 알람 내용
                    .alarmType(entity.getAlarmType())  // 알람 타입
                    .targetId(entity.getTargetId())  // 알람 대상 ID
                    .timeType(result.type().name())  // 알림 발생 시간 타입 ("HOUR" 또는 "DAY")
                    .timeValue(result.value())  // 알림 발생 시간 값
                    .build();
        }
    }

    // 알림 목록 조회
    public static List<AlarmRes> fromEntities(List<Alarm> entities) {
        return entities.stream()
                .map(AlarmRes::fromEntity)
                .collect(Collectors.toList());
    }

}
