package com.nookbook.domain.alarm.message;

import com.nookbook.domain.alarm.domain.Alarm;
import com.nookbook.domain.challenge.domain.repository.ChallengeRepository;
import com.nookbook.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AlarmRenderer {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    public String render(Alarm alarm) {
        Map<String, String> args = alarm.getArgs();

        return switch (alarm.getTemplate()) {
            case FRIEND_REQUEST, FRIEND_ACCEPTED, WAKE_UP -> {
                String nickname = getUserNickname(args.get("senderId"));
                yield alarm.getTemplate().format(List.of(nickname));
            }
            case CHALLENGE_INVITE, CHALLENGE_ACCEPTED -> {
                String nickname = getUserNickname(args.get("senderId"));
                String challengeName = getChallengeName(args.get("challengeId"));
                yield alarm.getTemplate().format(List.of(nickname, challengeName));
            }
        };
    }

    private String getUserNickname(String userId) {
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자")).getNickname();
    }

    private String getChallengeName(String challengeId) {
        return challengeRepository.findById(Long.parseLong(challengeId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 챌린지")).getTitle();
    }
}

