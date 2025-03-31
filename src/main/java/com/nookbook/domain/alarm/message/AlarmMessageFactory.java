package com.nookbook.domain.alarm.message;

import com.nookbook.domain.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AlarmMessageFactory {

    public AlarmMessageInfo create(AlarmMessageTemplate template, Map<String, String> args) {
        return new AlarmMessageInfo(template, args);
    }

    public AlarmMessageInfo createFriendRequest(Long senderId) {
        return create(AlarmMessageTemplate.FRIEND_REQUEST,
                Map.of("senderId", senderId.toString()));
    }

    public AlarmMessageInfo createFriendAccepted(Long senderId) {
        return create(AlarmMessageTemplate.FRIEND_ACCEPTED,
                Map.of("senderId", senderId.toString()));
    }

    public AlarmMessageInfo createWakeUp(Long senderId, Long challengeId) {
        return create(AlarmMessageTemplate.WAKE_UP,
                Map.of("senderId", senderId.toString(),
                        "challengeId", challengeId.toString()));
    }


    public AlarmMessageInfo createChallengeInvite(Long senderId, Long challengeId) {
        return create(AlarmMessageTemplate.CHALLENGE_INVITE,
                Map.of("senderId", senderId.toString(),
                        "challengeId", challengeId.toString()));
    }

    public AlarmMessageInfo createChallengeAccepted(Long senderId, Long challengeId) {
        return create(AlarmMessageTemplate.CHALLENGE_ACCEPTED,
                Map.of("senderId", senderId.toString(),
                        "challengeId", challengeId.toString()));
    }


}
