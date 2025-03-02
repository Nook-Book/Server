package com.nookbook.domain.challenge.application;

import com.nookbook.domain.book.BookNotFoundException;
import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.challenge.domain.*;
import com.nookbook.domain.challenge.domain.repository.ChallengeRepository;
import com.nookbook.domain.challenge.domain.repository.InvitationRepository;
import com.nookbook.domain.challenge.domain.repository.ParticipantRepository;
import com.nookbook.domain.challenge.dto.request.ChallengeCreateReq;
import com.nookbook.domain.challenge.dto.response.*;
import com.nookbook.domain.challenge.exception.*;
import com.nookbook.domain.common.BaseEntity;
import com.nookbook.domain.timer.application.TimerService;
import com.nookbook.domain.timer.domain.Timer;
import com.nookbook.domain.user.application.FriendService;
import com.nookbook.domain.user.domain.Friend;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final FriendService friendService;
    private final TimerService timerService;

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

        Long ChallengeId = challenge.getChallengeId();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(ChallengeId)
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

        // 초대 수락 대기중인 챌린지 목록
        List<Invitation> invitations = invitationRepository.findAllByUser(user);
        List<Challenge> waitingInvitationList = invitations.stream()
                .filter(invitation -> invitation.getInvitationStatus().equals(InvitationStatus.INVITING))
                .map(Invitation::getChallenge)
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
                .waitingInvitationCount(waitingInvitationList.size())
                .waitingInvitationList(
                        waitingInvitationList.stream()
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

    // 챌린지 상세 조회
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
                .dailyStartTime(challenge.getStartTime())
                .dailyEndTime(challenge.getEndTime())
                .participants(participants)
                .build();

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information(challengeDetailRes)
                .build();

        return ResponseEntity.ok(response);
    }


    // 참여자 정보 조회
    public List<ParticipantStatusListRes> getParticipantStatusList(User user, Challenge challenge) {
        List<Participant> participants = participantRepository.findAllByChallenge(challenge);
        // 오늘 생성된 userBook 기록 조회
        UserBook userBook = userBookRepository.findByUserAndCreatedAtAfter(user, LocalDate.now().atStartOfDay())
                .orElse(null);

        if(userBook == null) {
            return EmptyTodayUserBook(participants);
        }

        Book book = validateBook(userBook.getBook().getBookId());

        // 오늘 타이머 기록 조회
        List<Timer> todayTimers = timerService.getTodayTimers(userBook);

        // 가장 최근의 타이머 조회
        Timer timer = todayTimers.stream()
                .max(Comparator.comparing(BaseEntity::getCreatedAt))
                .orElse(null);

        // 읽고 있는지 여부, 읽은 시간
        boolean isReading = timer != null && timer.isReading();
        BigInteger readTime = timer != null ? timer.getReadTime() : BigInteger.ZERO;

        // 읽은 시간을 문자열로 변환
        String readTimeString = timerService.convertBigIntegerToString(readTime);

        // 참여자 목록 조회 응답 객체 build & return

        return participants.stream()
                .map(participant -> ParticipantStatusListRes.builder()
                        .participantId(participant.getParticipantId()) // 참가자 ID
                        .nickname(participant.getUser().getNickname()) // 참가자 닉네임
                        .readingBookTitle(book.getTitle()) // 읽고 있는 책 제목
                        .readingBookImage(book.getImage()) // 읽고 있는 책 이미지
                        .participantImage(participant.getUser().getImageUrl()) // 참가자 이미지
                        .isReading(isReading) // 실시간 독서 진행 여부
                        .dailyReadingTime(readTimeString) // 가장 최근의 독서 시간
                        .build())
                .toList();
    }

    // 오늘 userBook 기록이 없는 경우의 응답
    private List<ParticipantStatusListRes> EmptyTodayUserBook(List<Participant> participants) {
        return participants.stream()
                .map(participant -> ParticipantStatusListRes.builder()
                        .participantId(participant.getParticipantId()) // 참가자 ID
                        .nickname(participant.getUser().getNickname()) // 참가자 닉네임
                        .readingBookTitle("") // 읽고 있는 책 제목
                        .readingBookImage("") // 읽고 있는 책 이미지
                        .participantImage(participant.getUser().getImageUrl()) // 참가자 이미지
                        .isReading(false) // 실시간 독서 진행 여부
                        .dailyReadingTime("00:00:00") // 가장 최근의 독서 시간
                        .build())
                .toList();
    }


    // 챌린지 참가자 삭제
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


    // 챌린지 참가자 초대
    @Transactional
    public ResponseEntity<?> inviteParticipant(UserPrincipal userPrincipal, Long challengeId, Long participantId) {
        User user = validateUser(userPrincipal);
        User participant = userRepository.findById(participantId)
                .orElseThrow(UserNotFoundException::new);

        Challenge challenge = validateChallenge(challengeId);

        validateChallengeAuthorization(user, challenge); // 챌린지 owner만 참가자 초대 가능
        // 챌린지 참가자 초대
        invitationService.inviteParticipant(challenge, participant);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("참가 요청이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 챌린지 이미지 수정
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


    // 챌린지 정보 수정
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


    // 챌린지 삭제
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


    // 챌린지 방장 변경
    @Transactional
    public ResponseEntity<?> changeOwner(UserPrincipal userPrincipal, Long challengeId, Long newOwnerId) {
        User user = validateUser(userPrincipal);
        Challenge challenge = validateChallenge(challengeId);
        validateChallengeAuthorization(user, challenge);

        // newOwnerId는 participantId임
        Participant newOwner = participantService.getParticipant(newOwnerId);

        // 챌린지 방장 변경
        challenge.changeOwner(newOwner);
        challengeRepository.save(challenge);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("챌린지 방장 변경이 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    // 챌린지 참가자 목록 조회
    public ResponseEntity<?> getParticipantList(UserPrincipal userPrincipal, Long challengeId) {
        User user = validateUser(userPrincipal);

        Challenge challenge = validateChallenge(challengeId);
        List<Participant> participants = challenge.getParticipants();

        User owner = challenge.getOwner();

        List<ParticipantRes> participantResList = participants.stream()
                .map(participant -> ParticipantRes.builder()
                        .participantId(participant.getParticipantId())
                        .participantNickname((participant.getUser().getNickname()))
                        .participantImage(participant.getUser().getImageUrl())
                        .role(participantService.getParticipantRole(participant, owner))
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



    // 초대 가능한 친구 목록 조회
    public ResponseEntity<?> getInviteFriends(UserPrincipal userPrincipal, Long challengeId) {
        User user = validateUser(userPrincipal);
        Challenge challenge = validateChallenge(challengeId);

        // 사용자의 친구 목록 페이징 조회
        List<Friend> inviteFriendsPage = friendService.getFriends(user);

        // 해당 챌린지의 invitation 목록 조회
        List<Invitation> invitations = invitationRepository.findAllByChallenge(challenge);

        // DTO 변환
        List<ChallengeInvitationRes> challengeInvitationResPage = inviteFriendsPage.stream().map(friend -> {
            Long friendUserId = friend.getFriendUserId(user);  // 친구의 userId 가져오기
            return ChallengeInvitationRes.builder()
                    .userId(friendUserId)
                    .nickname(userService.findById(friendUserId).getNickname())  // userId로 닉네임 조회
                    .profileImage(userService.findById(friendUserId).getImageUrl())  // userId로 이미지 조회
                    .isInvitable(invitations.stream()
                            .noneMatch(invitation -> invitation.getUser().getUserId().equals(friendUserId)))
                    .build();
        }).toList();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(challengeInvitationResPage)
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    // 초대 수락
    @Transactional
    public ResponseEntity<?> acceptInvitation(UserPrincipal userPrincipal, Long challengeId) {
        User user = validateUser(userPrincipal);
        Challenge challenge = validateChallenge(challengeId);

        // 해당 챌린지의 invitation 목록 조회
        List<Invitation> invitations = invitationRepository.findAllByChallenge(challenge);

        // 해당 유저의 invitation 찾기
        Invitation invitation = invitations.stream()
                .filter(inv -> inv.getUser().equals(user))
                .findFirst()
                .orElseThrow();

        // 초대 수락
        invitationService.acceptInvitation(invitation.getInvitationId());

        // 챌린지 참가자(participant)로 등록
        participantService.saveParticipant(user, challenge);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("챌린지 참가가 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    // 초대 거절
    @Transactional
    public ResponseEntity<?> rejectInvitation(UserPrincipal userPrincipal, Long challengeId) {
        User user = validateUser(userPrincipal);
        Challenge challenge = validateChallenge(challengeId);

        // 해당 챌린지의 invitation 목록 조회
        List<Invitation> invitations = invitationRepository.findAllByChallenge(challenge);

        // 해당 유저의 invitation 찾기
        Invitation invitation = invitations.stream()
                .filter(inv -> inv.getUser().equals(user))
                .findFirst()
                .orElseThrow();

        // 초대 거절
        invitationService.rejectInvitation(invitation.getInvitationId());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("챌린지 참가 요청을 거절하였습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    // 챌린지 나가기 (방장이 아닌 경우만 가능)
    @Transactional
    public ResponseEntity<?> leaveChallenge(UserPrincipal userPrincipal, Long challengeId) {
        User user = validateUser(userPrincipal);
        Challenge challenge = validateChallenge(challengeId);

        // 참가자 검증
        Participant participant = validateParticipant(user, challenge);

        // 방장 여부 확인
        // 만약 사용자 본인이 방장이라면, 오류 발생
        validCanLeaveChallenge(user, challenge);

        // 참가자 삭제
        participantRepository.delete(participant);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("챌린지 탈퇴가 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);


    }

    // 챌린지 나가기 시 방장 여부 조회
    private void validCanLeaveChallenge(User user, Challenge challenge) {
        if (challenge.getOwner().equals(user)) {
            throw new ChallengeOwnerCantLeaveException();
        }
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

    // 챌린지 내 참가자 검증 메서드
    private Participant validateParticipant(User user, Challenge challenge) {
        return participantRepository.findByUserAndChallenge(user, challenge);
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
