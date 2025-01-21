package com.nookbook.domain.challenge.application;

import com.nookbook.domain.book.BookNotFoundException;
import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.challenge.domain.Challenge;
import com.nookbook.domain.challenge.domain.ChallengeStatus;
import com.nookbook.domain.challenge.domain.Participant;
import com.nookbook.domain.challenge.domain.repository.ChallengeRepository;
import com.nookbook.domain.challenge.domain.repository.InvitationRepository;
import com.nookbook.domain.challenge.domain.repository.ParticipantRepository;
import com.nookbook.domain.challenge.dto.request.ChallengeCreateReq;
import com.nookbook.domain.challenge.dto.response.*;
import com.nookbook.domain.challenge.exception.ChallengeNotAuthorizedException;
import com.nookbook.domain.challenge.exception.ChallengeNotFoundException;
import com.nookbook.domain.challenge.exception.ParticipantNotFoundException;
import com.nookbook.domain.challenge.exception.ParticipantNotInChallengeException;
import com.nookbook.infrastructure.s3.S3Uploader;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user.exception.UserNotFoundException;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final UserService userService;
    private final ChallengeRepository challengeRepository;
    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;
    private final InvitationRepository invitationRepository;
    private final InvitationService invitationService;
    private final S3Uploader s3Uploader;
    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // 챌린지 생성
    @Transactional
    public ResponseEntity<?> createChallenge(
            UserPrincipal userPrincipal, ChallengeCreateReq challengeCreateReq, MultipartFile challengeCover)
    {
        // 사용자 검증
        User user = validateUser(userPrincipal);
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
                .startTime(challengeCreateReq.getStartTime())
                .endTime(challengeCreateReq.getEndTime())
                .challengeStatus(challengeStatus)
                .participants(new ArrayList<>())
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
        // 사용자 검증
        User user = validateUser(userPrincipal);
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
        // 사용자 검증
        User user = validateUser(userPrincipal);
        // 챌린지 검증
        Challenge challenge = validateChallenge(challengeId);

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
        Book book = validateBook(userBook.getBook().getBookId());
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
    @Transactional
    public ResponseEntity<?> deleteParticipant(UserPrincipal userPrincipal, Long challengeId, Long participantId) {
        User user = validateUser(userPrincipal);
        Challenge challenge = validateChallenge(challengeId);
        validateChallengeAuthorization(user, challenge);
        Participant participant = validateParticipant(participantId);
        validateParticipantInChallenge(participant, challenge);

        // 참가자 삭제
        participantRepository.delete(participant);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("참가자 삭제가 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @Transactional
    public ResponseEntity<?> inviteParticipant(UserPrincipal userPrincipal, Long challengeId, Long participantId) {
        User user = validateUser(userPrincipal);
        User participant = userRepository.findById(participantId)
                .orElseThrow(UserNotFoundException::new);
        Challenge challenge = validateChallenge(challengeId);
        validateChallengeAuthorization(user, challenge); // 챌린지 owner만 참가자 초대 가능
        // 챌린지 참가자 추가
        invitationService.inviteParticipant(challenge, participant);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("참가자 추가가 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public ResponseEntity<?> updateChallengeImage(UserPrincipal userPrincipal, Long challengeId, MultipartFile challengeCover) {
        User user = validateUser(userPrincipal);
        Challenge challenge = validateChallenge(challengeId);
        validateChallengeAuthorization(user, challenge);

        // 기존 이미지 삭제
        s3Uploader.deleteFile(challenge.getChallengeCover());
        // 커버 이미지 s3 업로드
        String coverImageUrl = s3Uploader.uploadImage(challengeCover);

        // 챌린지 이미지 수정
        challenge.updateChallengeCover(coverImageUrl);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("챌린지 이미지 수정이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @Transactional
    public ResponseEntity<?> updateChallengeInfo(UserPrincipal userPrincipal, Long challengeId, ChallengeCreateReq challengeUpdateReq) {
        User user = validateUser(userPrincipal);
        Challenge challenge = validateChallenge(challengeId);
        validateChallengeAuthorization(user, challenge);

        // 데이터 포맷
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(challengeUpdateReq.getStartDate(), dateFormatter);
        LocalDate endDate = LocalDate.parse(challengeUpdateReq.getEndDate(), dateFormatter);

        // Update challenge information
        challenge.updateChallengeInfo(
                challengeUpdateReq.getTitle(),
                startDate,
                endDate,
                challengeUpdateReq.getDailyGoal(),
                challengeUpdateReq.getStartTime(),
                challengeUpdateReq.getEndTime()
        );

        challengeRepository.save(challenge);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("챌린지 정보 수정이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @Transactional
    public ResponseEntity<?> deleteChallenge(UserPrincipal userPrincipal, Long challengeId) {
        User user = validateUser(userPrincipal);
        Challenge challenge = validateChallenge(challengeId);
        validateChallengeAuthorization(user, challenge);

        // 챌린지 삭제
        challengeRepository.delete(challenge);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("챌린지 삭제가 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> changeOwner(UserPrincipal userPrincipal, Long challengeId, Long newOwnerId) {
        User user = validateUser(userPrincipal);
        Challenge challenge = validateChallenge(challengeId);
        validateChallengeAuthorization(user, challenge);

        User newOwner = userRepository.findById(newOwnerId)
                .orElseThrow(UserNotFoundException::new);

        // 챌린지 방장 변경
        challenge.changeOwner(newOwner);
        challengeRepository.save(challenge);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("챌린지 방장 변경이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<?> getParticipantList(UserPrincipal userPrincipal, Long challengeId) {
        User user = validateUser(userPrincipal);

        Challenge challenge = validateChallenge(challengeId);
        List<Participant> participants = challenge.getParticipants();

        List<ParticipantRes> participantResList = participants.stream()
                .map(participant -> ParticipantRes.builder()
                        .participantId(participant.getParticipantId())
                        .participantNickname((participant.getUser().getNickname()))
                        .participantImage(participant.getUser().getImageUrl())
                        .role(participantService.getParticipantRole(challenge, participant))
                        .build())
                .toList();

        ParticipantListRes participantListRes = ParticipantListRes.builder()
                .isOwner(challenge.getOwner().equals(user))
                .participantList(participantResList)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(participantListRes)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 사용자 검증 메서드
    private User validateUser(UserPrincipal userPrincipal) {
//        return userService.findByEmail(userPrincipal.getEmail())
//                .orElseThrow(UserNotFoundException::new);
        // userId=1L로 고정
        return userService.findById(1L);
    }

    // 챌린지 검증 메서드
    private Challenge validateChallenge(Long challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(ChallengeNotFoundException::new);
    }

    // 참가자 검증 메서드
    private Participant validateParticipant(Long participantId) {
        return participantRepository.findById(participantId)
                .orElseThrow(ParticipantNotFoundException::new);
    }

    // 챌린지 수정 권한 검증
    private void validateChallengeAuthorization(User user, Challenge challenge) {
        if (!challenge.getOwner().equals(user)) {
            throw new ChallengeNotAuthorizedException();
        }
    }

    // 참가자가 해당 챌린지에 속하는지 검증하는 메서드
    private void validateParticipantInChallenge(Participant participant, Challenge challenge) {
        if (!participant.getChallenge().equals(challenge)) {
            throw new ParticipantNotInChallengeException();
        }
    }

    // 책 검증 메서드
    private Book validateBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);
    }

}
