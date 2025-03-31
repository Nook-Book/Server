package com.nookbook.domain.alarm.message;

import com.nookbook.domain.alarm.domain.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum AlarmMessageTemplate {

    // 알람 타입에 따른 메시지 템플릿
    // FRIEND: 친구 요청 관련 알림
    FRIEND_REQUEST(AlarmType.FRIEND, "%s님이 회원님에게 친구 요청을 보냈습니다."),
    FRIEND_ACCEPTED(AlarmType.FRIEND, "%s님이 회원님의 친구 요청을 수락했습니다."),

    // CHALLENGE: 챌린지 초대 관련 알림
    CHALLENGE_INVITE(AlarmType.CHALLENGE, "%s님이 회원님을 %s 챌린지에 초대했습니다."),
    CHALLENGE_ACCEPTED(AlarmType.CHALLENGE, "%s님이 회원님의 %s 챌린지 초대를 수락했습니다."),

    // WAKE_UP: 깨우기 관련 알림
    WAKE_UP(AlarmType.CHALLENGE, "%s님이 회원님을 깨웠습니다.");

    private final AlarmType type;
    private final String template;

    public String format(List<String> args) {
        return String.format(template, args.toArray());
    }

}