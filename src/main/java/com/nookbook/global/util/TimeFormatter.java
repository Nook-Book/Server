package com.nookbook.global.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeFormatter {
    public enum TimeType {
        HOUR, DAY
    }

    // 시간을 표시하는 클래스 (TimeType: 시간 단위, value: 시간 값)
    public record TimeResult(TimeType type, int value) {
    }

    public static TimeResult formatToTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now(); // 현재 시간
        // 생성된 시간과 현재 시간의 차이
        Duration duration = Duration.between(createdAt, now);
        long hours = duration.toHours(); // 시간 단위로 변환

        if (hours < 24) {
            return new TimeResult(TimeType.HOUR, (int) hours);
        } else {
            int days = (int) duration.toDays();
            return new TimeResult(TimeType.DAY, days);
        }
    }
}
