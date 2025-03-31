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
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nookbook.global.payload.ApiResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> getAllAlarms(UserPrincipal userPrincipal, int page, int size) {
        User user = getUser(userPrincipal);
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Alarm> alarmsPage = alarmRepository.findByUserAndCreatedAtAfter(user, weekAgo, pageable);

        List<AlarmRes> alarmListRes = AlarmRes.fromEntities(alarmsPage.getContent(), alarmRenderer);

        Map<String, Object> result = new HashMap<>();
        result.put("alarms", alarmListRes);
        result.put("totalElements", alarmsPage.getTotalElements());
        result.put("totalPages", alarmsPage.getTotalPages());
        result.put("currentPage", alarmsPage.getNumber());
        result.put("isLast", alarmsPage.isLast());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(result)
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

    private User getUser(UserPrincipal userPrincipal) {
        return userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(UserNotFoundException::new);
    }

    public void deleteFriendRequestAlarm(User receiver, Long userId) {
        // receiver의 모든 알림 중에서 AlarmType이 FRIEND이고 senderId가 userId인 알림을 삭제
        // 오래된 알림은 자동으로 삭제되므로, 알림이 존재하는 경우에만 삭제
        List<Alarm> alarms = alarmRepository.findByUserAndAlarmTypeAndSenderId(receiver, AlarmType.FRIEND, userId);
        DefaultAssert.isTrue(!alarms.isEmpty(), "삭제할 알림이 없습니다.");
        alarmRepository.deleteAll(alarms);
    }
}
