package com.nookbook.domain.challenge.application;

import com.nookbook.domain.challenge.domain.Challenge;
import com.nookbook.domain.challenge.domain.Participant;
import com.nookbook.domain.challenge.domain.ParticipantStatus;
import com.nookbook.domain.challenge.domain.repository.ChallengeRepository;
import com.nookbook.domain.challenge.domain.repository.ParticipantRepository;
import com.nookbook.domain.challenge.dto.request.ChallengeCreateReq;
import com.nookbook.domain.s3.application.S3Uploader;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final UserService userService;
    private final ChallengeRepository challengeRepository;
    private final ParticipantRepository participantRepository;
    private final S3Uploader s3Uploader;

    // 챌린지 생성
    @Transactional
    public ResponseEntity<?> createChallenge(
            UserPrincipal userPrincipal, ChallengeCreateReq challengeCreateReq, MultipartFile challengeCover)
    {
        // 사용자 검증
        User user= userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        // 커버 이미지 s3 업로드
        String coverImageUrl = s3Uploader.uploadImage(challengeCover);

        // 챌린지 생성 로직
        Challenge challenge = Challenge.builder()
                .title(challengeCreateReq.getTitle())
                .challengeCover(coverImageUrl)
                .startDate(LocalDateTime.parse(challengeCreateReq.getStartDate()))
                .endDate(LocalDateTime.parse(challengeCreateReq.getEndDate()))
                .dailyGoal(challengeCreateReq.getDailyGoal())
                .build();

        challengeRepository.save(challenge);

        // 챌린지를 생성한 유저는 participant로 등록
        Participant participant = Participant.builder()
                .user(user)
                .challenge(challenge)
                .participantStatus(ParticipantStatus.RESTING)
                .build();

        participantRepository.save(participant);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("챌린지 생성이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
