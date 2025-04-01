package com.nookbook.global.notification;


import io.github.jav.exposerversdk.ExpoPushMessage;
import io.github.jav.exposerversdk.ExpoPushTicket;
import io.github.jav.exposerversdk.PushClient;
import io.github.jav.exposerversdk.PushClientException;
import io.github.jav.exposerversdk.enums.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ExpoNotificationSender {

    private final PushClient pushClient;

    public ExpoNotificationSender() {
        try {
            this.pushClient = new PushClient();
        } catch (PushClientException e) {
            throw new RuntimeException("Expo PushClient 초기화 실패", e);
        }
    }

    /**
     * 푸시 알림 전송
     * @param expoPushToken 대상 사용자 토큰
     * @param title 알림 제목
     * @param body 알림 본문
     * @param data 클릭 시 전달할 데이터 (예: alarmId, type 등)
     */
    public void send(String expoPushToken, String title, String body, Map<String, Object> data) {
        if (!PushClient.isExponentPushToken(expoPushToken)) {
            log.warn("유효하지 않은 Expo Push Token: {}", expoPushToken);
            return;
        }

        ExpoPushMessage message = new ExpoPushMessage();
        message.setTo(Collections.singletonList(expoPushToken));
        message.setTitle(title);
        message.setBody(body);
        message.setData(data);

        List<ExpoPushMessage> messages = Collections.singletonList(message);

        try {
            // 비동기 방식으로 티켓 요청
            CompletableFuture<List<ExpoPushTicket>> future = pushClient.sendPushNotificationsAsync(messages);
            List<ExpoPushTicket> tickets = future.get(); // blocking wait

            for (ExpoPushTicket ticket : tickets) {
                if (Status.ERROR.equals(ticket.getStatus())) {
                    log.error("푸시 알림 전송 실패: {}", ticket.getMessage());
                } else {
                    log.info("푸시 알림 전송 성공: {}", ticket.getId());
                }
            }

        } catch (Exception e) {
            log.error("Expo 알림 전송 중 예외 발생", e);
        }
    }
}