package com.nookbook.domain.challenge.application;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.challenge.domain.Challenge;
import com.nookbook.domain.challenge.domain.ChallengeStatus;
import com.nookbook.domain.challenge.domain.Participant;
import com.nookbook.domain.challenge.domain.repository.ChallengeRepository;
import com.nookbook.domain.challenge.domain.repository.ParticipantRepository;
import com.nookbook.domain.challenge.dto.request.ChallengeCreateReq;
import com.nookbook.domain.challenge.dto.response.ChallengeDetailRes;
import com.nookbook.domain.challenge.dto.response.ChallengeListDetailRes;
import com.nookbook.domain.challenge.dto.response.ChallengeListRes;
import com.nookbook.domain.challenge.dto.response.ParticipantStatusListRes;
import com.nookbook.domain.s3.application.S3Uploader;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user_book.domain.UserBook;
import com.nookbook.domain.user_book.domain.repository.UserBookRepository;
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
    private final ParticipantRepository participantRepository;
    private final S3Uploader s3Uploader;
    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;

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
                .owner(user)
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

    public ResponseEntity<?> getChallengeDetail(UserPrincipal userPrincipal, Long challengeId) {
        User user = userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("챌린지 정보를 찾을 수 없습니다."));

        Boolean isEditable = challenge.getOwner().equals(user);
        List<ParticipantStatusListRes> participants = getParticipantStatusList(user, challenge);

        // 챌린지 총 시간 계산
        int totalDate  = (int) (challenge.getEndDate().toEpochDay() - challenge.getStartDate().toEpochDay());
        Integer dailyGoal = challenge.getDailyGoal();

        int totalHour = 0;
        if (dailyGoal != null) {
            totalHour = totalDate * dailyGoal / 60;
        }

        ChallengeDetailRes challengeDetailRes = ChallengeDetailRes.builder()
                .challengeId(challenge.getChallengeId())
                .isEditable(isEditable)
                .title(challenge.getTitle())
                .challengeCover(challenge.getChallengeCover())
                .challengeStatus(challenge.getChallengeStatus())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .totalHour(totalHour)
                .dailyGoal(challenge.getDailyGoal())
                .participants(participants)
                .build();

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information(challengeDetailRes)
                .build();

        return ResponseEntity.ok(response);
    }


    // 참여자 목록 조회
    public List<ParticipantStatusListRes> getParticipantStatusList(User user, Challenge challenge) {
        List<Participant> participants = participantRepository.findAllByChallenge(challenge);
        // 가장 최근 읽고 있는 / 읽었던 책 정보
        UserBook userBook = userBookRepository.findFirstByUserOrderByUpdatedAtDesc(user);
        Book book = bookRepository.findById(userBook.getBook().getBookId())
                .orElseThrow(() -> new RuntimeException("책 정보를 찾을 수 없습니다."));
        // 참여자 목록 조회
        List<ParticipantStatusListRes> participantStatusListRes = participants.stream()
                .map(participant -> ParticipantStatusListRes.builder()
                        .participantId(participant.getUser().getUserId())
                        .nickname(participant.getUser().getNickname())
                        .readingBookTitle(book.getTitle())
                        .readingBookImage(book.getTitle())
                        .participantImage(participant.getUser().getImageUrl())
                        .participantStatus(participant.getParticipantStatus())
                        .build())
                .toList();

        return participantStatusListRes;
    }
}
