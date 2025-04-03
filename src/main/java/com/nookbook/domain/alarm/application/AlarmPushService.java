package com.nookbook.domain.alarm.application;

import com.nookbook.domain.alarm.domain.Alarm;
import com.nookbook.domain.alarm.message.AlarmMessageTemplate;
import com.nookbook.domain.alarm.message.AlarmRenderer;
import com.nookbook.domain.user.domain.User;
import com.nookbook.global.notification.ExpoNotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlarmPushService {

    private final ExpoNotificationSender expoNotificationSender;
    private final AlarmRenderer alarmRenderer;

    public void send(User receiver, Alarm alarm) {
        String expoPushToken = receiver.getExpoPushToken();
        if (expoPushToken == null || expoPushToken.isBlank()) return;

        String receiverNickname = receiver.getNickname();

        String title = getPushTitle(alarm.getTemplate(), receiverNickname);
        String body = alarmRenderer.render(alarm);

        expoNotificationSender.send(
                expoPushToken,
                title,
                body,
                Map.of(
                        "alarmId", alarm.getAlarmId(),
                        "type", alarm.getAlarmType().name(),
                        "targetId", String.valueOf(alarm.getTargetId())
                )
        );
    }

    // 템플릿에 따라 푸시 제목을 설정하는 메서드
    private String getPushTitle(AlarmMessageTemplate template, String receiverNickname) {
        return switch (template) {
            case FRIEND_REQUEST -> receiverNickname + "친구 요청";
            case FRIEND_ACCEPTED -> receiverNickname + "친구 요청 수락됨";
            case WAKE_UP -> receiverNickname + "님, 책 읽을 시간이에요!";
            case CHALLENGE_INVITE -> receiverNickname + "님, 챌린지 함께해요!";
            case CHALLENGE_ACCEPTED -> receiverNickname + "챌린지 초대 수락됨";
        };
    }

}
