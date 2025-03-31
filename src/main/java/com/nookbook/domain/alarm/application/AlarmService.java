package com.nookbook.domain.alarm.application;

import com.nookbook.domain.alarm.domain.Alarm;
import com.nookbook.domain.alarm.domain.AlarmType;
import com.nookbook.domain.alarm.domain.repository.AlarmRepository;
import com.nookbook.domain.alarm.dto.response.AlarmRes;
import com.nookbook.domain.alarm.message.AlarmMessageFactory;
import com.nookbook.domain.alarm.message.AlarmMessageInfo;
import com.nookbook.domain.alarm.message.AlarmRenderer;
import com.nookbook.domain.challenge.domain.Challenge;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.exception.UserNotFoundException;
import com.nookbook.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    private final UserService userService;
    private final AlarmRepository alarmRepository;
    private final AlarmMessageFactory alarmMessageFactory;
    private final AlarmRenderer alarmRenderer;


    // 알림 목록 조회
    public ResponseEntity<?> getAllAlarms(UserPrincipal userPrincipal) {
        User user = getUser(userPrincipal);

        // 최근 7일간의 알림만 조회
        List<Alarm> alarms = filterRecentAlarms(user.getAlarms());

        // 알림 응답 변환 (renderer 주입)
        List<AlarmRes> alarmListRes = AlarmRes.fromEntities(alarms, alarmRenderer);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(alarmListRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }



    // 친구 요청 알림 전송/저장
    @Transactional
    public void sendFriendRequestAlarm(User sender, User receiver) {
        AlarmMessageInfo info = alarmMessageFactory.createFriendRequest(sender.getUserId());

        Alarm alarm = Alarm.create(
                receiver,
                sender.getUserId(),
                AlarmType.FRIEND,
                info.template(),
                info.args(),
                sender.getUserId() // targetId: sender를 클릭 시 이동하도록
        );

        alarmRepository.save(alarm);
    }

    // 친구 수락 알림 전송/저장
    @Transactional
    public void sendFriendAcceptedAlarm(User sender, User receiver) {
        AlarmMessageInfo info = alarmMessageFactory.createFriendAccepted(sender.getUserId());

        Alarm alarm = Alarm.create(
                receiver,
                sender.getUserId(),
                AlarmType.FRIEND,
                info.template(),
                info.args(),
                sender.getUserId()
        );

        alarmRepository.save(alarm);
    }

    // 챌린지 참가자 깨우기 알림 전송/저장
    @Transactional
    public void sendChallengeParticipantWakeUpAlarm(User sender, User receiver, Challenge challenge) {
        AlarmMessageInfo info = alarmMessageFactory.createWakeUp(sender.getUserId(), challenge.getChallengeId());

        Alarm alarm = Alarm.create(
                receiver,
                sender.getUserId(),
                AlarmType.CHALLENGE,
                info.template(),
                info.args(),
                challenge.getChallengeId()
        );

        alarmRepository.save(alarm);
    }

    // 챌린지 초대 알림 전송/저장
    @Transactional
    public void sendChallengeInviteAlarm(User sender, User receiver, Challenge challenge) {
        AlarmMessageInfo info = alarmMessageFactory.createChallengeInvite(sender.getUserId(), challenge.getChallengeId());

        Alarm alarm = Alarm.create(
                receiver,
                sender.getUserId(),
                AlarmType.CHALLENGE,
                info.template(),
                info.args(),
                challenge.getChallengeId()
        );

        alarmRepository.save(alarm);
    }

    // 챌린지 초대 수락 알림 전송/저장
    // 챌린지 초대 수락 시, 방장에게 알림 전송
    @Transactional
    public void sendChallengeAcceptedAlarm(User sender, User receiver, Challenge challenge) {
        AlarmMessageInfo info = alarmMessageFactory.createChallengeAccepted(sender.getUserId(), challenge.getChallengeId());

        Alarm alarm = Alarm.create(
                receiver,
                sender.getUserId(),
                AlarmType.CHALLENGE,
                info.template(),
                info.args(),
                challenge.getChallengeId()
        );

        alarmRepository.save(alarm);
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
