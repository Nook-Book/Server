package com.nookbook.domain.challenge.application;

import com.nookbook.domain.challenge.domain.Challenge;
import com.nookbook.domain.challenge.domain.ChallengeStatus;
import com.nookbook.domain.challenge.domain.repository.ChallengeRepository;
import com.nookbook.domain.challenge.domain.repository.ParticipantRepository;
import com.nookbook.domain.challenge.dto.request.ChallengeCreateReq;
import com.nookbook.domain.challenge.dto.response.ChallengeListDetailRes;
import com.nookbook.domain.challenge.dto.response.ChallengeListRes;
import com.nookbook.domain.s3.application.S3Uploader;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final UserService userService;
    private final ChallengeRepository challengeRepository;
    private final ParticipantService participantService;
    private final S3Uploader s3Uploader;
    private final ParticipantRepository participantRepository;

    // 챌린지 생성
    @Transactional
    public ResponseEntity<?> createChallenge(
            UserPrincipal userPrincipal, ChallengeCreateReq challengeCreateReq, MultipartFile challengeCover)
    {
        // 사용자 검증
        User user = userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        // 커버 이미지 s3 업로드
        String coverImageUrl = s3Uploader.uploadImage(challengeCover);

        // 날짜 파싱
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(challengeCreateReq.getStartDate(), formatter);
        LocalDate endDate = LocalDate.parse(challengeCreateReq.getEndDate(), formatter);

        ChallengeStatus challengeStatus;
        LocalDate today = LocalDate.now();

        // 시작일이 오늘 이후인 경우 -> Waiting
        if(startDate.isAfter(today)) {
            challengeStatus = ChallengeStatus.WAITING;
        }
        // 오늘이 시작일 이후, 종료일 이전인 경우 -> Progress
        else if(today.isEqual(startDate) || (startDate.isBefore(today) && endDate.isAfter(today))) {
            challengeStatus = ChallengeStatus.PROGRESS;
        }
        // 종료일이 오늘이거나 이전인 경우 -> End
        else {
            challengeStatus = ChallengeStatus.END;
        }

        // 챌린지 생성 로직
        Challenge challenge = Challenge.builder()
                .title(challengeCreateReq.getTitle())
                .challengeCover(coverImageUrl)
                .startDate(startDate)  // LocalDate를 LocalDateTime으로 변환하여 저장
                .endDate(endDate) // 종료일을 마지막 시간으로 설정
                .dailyGoal(challengeCreateReq.getDailyGoal())
                .challengeStatus(challengeStatus)
                .build();

        // 새로운 챌린지 저장
        challengeRepository.save(challenge);

        // 챌리지를 생성한 유저는 participant로 등록
        participantService.saveParticipant(user, challenge);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("챌린지 생성이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 유저가 참여중인 챌린지 목록 조회 (현재 진행중인 챌린지 / 종료된 챌린지)
    public ResponseEntity<?> getChallengeList(UserPrincipal userPrincipal) {
        User user = userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        // 사용자가 참여한 챌린지 목록 조회
        List<Challenge> challengeList = challengeRepository.findAllByUserParticipant(user);

        // 대기중인 챌린지 목록
        List<Challenge> waitingChallengeList = challengeList.stream()
                .filter(challenge -> challenge.getChallengeStatus().equals(ChallengeStatus.WAITING))
                .toList();

        // 진행중인 챌린지 목록
        List<Challenge> progressChallengeList = challengeList.stream()
                .filter(challenge -> challenge.getChallengeStatus().equals(ChallengeStatus.PROGRESS))
                .toList();

        // 종료된 챌린지 목록
        List<Challenge> endChallengeList = challengeList.stream()
                .filter(challenge -> challenge.getChallengeStatus().equals(ChallengeStatus.END))
                .toList();

        ChallengeListRes challengeListRes = ChallengeListRes.builder()
                .waitingCount(waitingChallengeList.size())
                .waitingList(
                        waitingChallengeList.stream()
                                .map(challenge -> ChallengeListDetailRes.builder()
                                        .challengeId(challenge.getChallengeId())
                                        .title(challenge.getTitle())
                                        .challengeCover(challenge.getChallengeCover())
                                        .build())
                                .toList()
                )
                .progressCount(progressChallengeList.size())
                .progressList(
                        progressChallengeList.stream()
                                .map(challenge -> ChallengeListDetailRes.builder()
                                        .challengeId(challenge.getChallengeId())
                                        .title(challenge.getTitle())
                                        .challengeCover(challenge.getChallengeCover())
                                        .build())
                                .toList()
                )
                .endCount(endChallengeList.size())
                .endList(
                        endChallengeList.stream()
                                .map(challenge -> ChallengeListDetailRes.builder()
                                        .challengeId(challenge.getChallengeId())
                                        .title(challenge.getTitle())
                                        .challengeCover(challenge.getChallengeCover())
                                        .build())
                                .toList()
                )
                .build();

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information(challengeListRes)
                .build();

        return ResponseEntity.ok(response);
    }
}
