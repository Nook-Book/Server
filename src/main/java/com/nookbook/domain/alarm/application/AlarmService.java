package com.nookbook.domain.alarm.application;

import com.nookbook.domain.alarm.domain.Alarm;
import com.nookbook.domain.alarm.domain.repository.AlarmRepository;
import com.nookbook.domain.alarm.dto.response.AlarmListRes;
import com.nookbook.domain.alarm.dto.response.AlarmRes;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.exception.UserNotFoundException;
import com.nookbook.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nookbook.global.payload.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

    AlarmRepository alarmRepository;
    private final UserService userService;

    // 알림 목록 조회
    public ResponseEntity<?> getAllAlarms(UserPrincipal userPrincipal) {
        User user = getUser(userPrincipal);
        // 최근 7일간의 알림만 조회
        List<Alarm> alarms = filterRecentAlarms(user.getAlarms());
        List<AlarmRes> alarmListRes = alarms.stream()
                .map(AlarmRes::fromEntity)
                .toList();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(alarmListRes)
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    // 최근 7일간의 알림만 필터링
    public List<Alarm> filterRecentAlarms(List<Alarm> alarms) {
        return alarms.stream()
                .filter(alarm -> alarm.getCreatedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .toList();
    }

    private User getUser(UserPrincipal userPrincipal) {
        return userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(UserNotFoundException::new);
    }
}
