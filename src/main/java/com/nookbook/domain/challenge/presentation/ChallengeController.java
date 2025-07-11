package com.nookbook.domain.challenge.presentation;

import com.nookbook.domain.challenge.application.ChallengeService;
import com.nookbook.domain.challenge.application.ParticipantService;
import com.nookbook.domain.challenge.dto.request.ChallengeCreateReq;
import com.nookbook.domain.challenge.dto.response.ChallengeDetailRes;
import com.nookbook.domain.challenge.dto.response.ChallengeInvitationRes;
import com.nookbook.domain.challenge.dto.response.ChallengeListRes;
import com.nookbook.domain.challenge.dto.response.ParticipantListRes;
import com.nookbook.domain.user_book.application.UserBookService;
import com.nookbook.domain.user_book.dto.response.DailyUserBookCalendarRes;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.CommonApiResponse;
import com.nookbook.global.payload.ErrorResponse;
import com.nookbook.global.payload.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenges")
@Tag(name = "Challenge", description = "챌린지 관련 API입니다.")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final UserBookService userBookService;
    private final ParticipantService participantService;

    // 새 챌린지 생성 API
    @Operation(summary = "새 챌린지 생성 API", description = "새 챌린지를 생성하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 생성 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 생성 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/new")
    public ResponseEntity<?> createChallenge(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @RequestPart MultipartFile challengeCover,
            @RequestPart ChallengeCreateReq challengeCreateReq
    ) {
        return challengeService.createChallenge(userPrincipal, challengeCreateReq, challengeCover);
    }

    // 챌린지 목록 조회 API
    @Operation(summary = "챌린지 목록 조회 API", description = "챌린지 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ChallengeListRes.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("")
    public ResponseEntity<?> getChallengeList(
            @Parameter @CurrentUser UserPrincipal userPrincipal
    ) {
        return challengeService.getChallengeList(userPrincipal);
    }

    // 챌린지 상세 조회 API
    @Operation(summary = "챌린지 상세 조회 API", description = "챌린지 상세 정보를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 상세 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ChallengeDetailRes.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 상세 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{challengeId}")
    public ResponseEntity<?> getChallengeDetail(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId
    ) {
        ChallengeDetailRes detail = challengeService.getChallengeDetail(userPrincipal, challengeId);
        return ResponseEntity.ok(CommonApiResponse.success(detail));
    }

    // 챌린지 참가자 삭제 API
    @Operation(summary = "챌린지 참가자 삭제 API", description = "챌린지 참가자를 삭제하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 참가자 삭제 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 참가자 삭제 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping("/{challengeId}/participants/{participantId}")
    public ResponseEntity<?> deleteParticipant(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId,
            @Parameter(description = "참가자 ID") @PathVariable Long participantId
    ) {
        return challengeService.deleteParticipant(userPrincipal, challengeId, participantId);
    }

    // 챌린지 참가 요청(초대) API
    @Operation(summary = "챌린지 참가자 초대 요청 API", description = "챌린지에 참가자를 초대하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 참가자 초대 요청 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 참가자 초대 요청 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/{challengeId}/participants/{participantId}")
    public ResponseEntity<?> addParticipant(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId,
            @Parameter(description = "참가자 ID") @PathVariable Long participantId
    ) {
        return challengeService.inviteParticipant(userPrincipal, challengeId, participantId);
    }

    // 챌린지 이미지 수정 API
    @Operation(summary = "챌린지 이미지 수정 API", description = "챌린지 이미지를 수정하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 이미지 수정 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 이미지 수정 실���", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/{challengeId}/image")
    public ResponseEntity<?> updateChallengeImage(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId,
            @RequestPart MultipartFile challengeCover
    ) {
        return challengeService.updateChallengeImage(userPrincipal, challengeId, challengeCover);
    }

    // 챌린지 정보 수정 API
    @Operation(summary = "챌린지 정보 수정 API", description = "챌린지 정보를 수정하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 정보 수정 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 정보 수정 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/{challengeId}")
    public ResponseEntity<?> updateChallengeInfo(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId,
            @RequestBody ChallengeCreateReq challengeUpdateReq
    ) {
        return challengeService.updateChallengeInfo(userPrincipal, challengeId, challengeUpdateReq);
    }

    // 챌린지 삭제 API
    @Operation(summary = "챌린지 삭제 API", description = "챌린지를 삭제하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 삭제 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 삭제 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<?> deleteChallenge(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId
    ) {
        return challengeService.deleteChallenge(userPrincipal, challengeId);
    }

    // 챌린지 방장 변경 API
    @Operation(summary = "챌린지 방장 변경 API", description = "챌린지 방장을 변경하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 방장 변경 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 방장 변경 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/{challengeId}/owner/{newOwnerId}")
    public ResponseEntity<?> changeOwner(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId,
            @Parameter(description = "새로운 방장 ID") @PathVariable Long newOwnerId
    ) {
        return challengeService.changeOwner(userPrincipal, challengeId, newOwnerId);
    }

    // 챌린지 참가자 목록 조회 (방장인지/일반인지에 대한 여부 정보 포함)
    @Operation(summary = "챌린지 참가자 목록 조회 API", description = "챌린지 참가자 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 참가자 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ParticipantListRes.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 참가자 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{challengeId}/participants")
    public ResponseEntity<?> getParticipantList(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId
    ) {
        return challengeService.getParticipantList(userPrincipal, challengeId);
    }

    // 챌린지에 초대할 친구 목록 조회 API
    // 페이징 처리 필요
    @Operation(summary = "챌린지에 초대할 친구 목록 조회 API", description = "챌린지에 초대할 친구 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지에 초대할 친구 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = ChallengeInvitationRes.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지에 초대할 친구 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{challengeId}/friends")
    public ResponseEntity<?> getInviteFriends(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId
    ) {
        return challengeService.getInviteFriends(userPrincipal, challengeId);
    }

    // 챌린지 초대 수락 API
    @Operation(summary = "챌린지 초대 수락 API", description = "챌린지 초대를 수락하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 초대 수락 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 초대 수락 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/{challengeId}/accept")
    public ResponseEntity<?> acceptInvitation(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId
    ) {
        return challengeService.acceptInvitation(userPrincipal, challengeId);
    }

    // 챌린지 초대 거절 API
    @Operation(summary = "챌린지 초대 거절 API", description = "챌린지 초대를 거절하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 초대 거절 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 초대 거절 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/{challengeId}/reject")
    public ResponseEntity<?> rejectInvitation(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId
    ) {
        return challengeService.rejectInvitation(userPrincipal, challengeId);
    }

    // 챌린지 나가기 API
    @Operation(summary = "챌린지 나가기 API", description = "참여 중인 챌린지에서 나가는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 나가기 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 나가기 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping("/{challengeId}/exit")
    public ResponseEntity<?> leaveChallenge(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId
    ) {
        return challengeService.leaveChallenge(userPrincipal, challengeId);
    }


    // 챌린지 참가자의 독서 기록 정보 조회 API
    // 날짜 형식: 2021-11-01 또는 2021-11
    // 삭제된 API
    @Operation(summary = "챌린지 참가자의 날짜별 독서 기록 조회", description = "챌린지 참가자의 날짜별 독서 기록을 조회합니다.")
    @GetMapping("/participants/{participantId}/calendar/{date}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "독서 캘린더 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = DailyUserBookCalendarRes.class)) } ),
            @ApiResponse(responseCode = "400", description = "독서 캘린더 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @Deprecated
    public ResponseEntity<?> getUserBookCalendar(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "참가자 ID를 입력해주세요.", required = true) @PathVariable Long participantId,
            @Parameter(description = "조회할 날짜를 입력해주세요.", example = "2021-11-01", required = true) @PathVariable String date
    ) {
        return userBookService.getUserBookCalendar(userPrincipal, participantId, date);
    }

    // 챌린지 멤버 깨우기
    @Operation(summary = "챌린지 멤버 깨우기 API", description = "챌린지 멤버에게 깨우기 알림을 보내는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 멤버 깨우기 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 멤버 깨우기 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/{challengeId}/participants/{participantId}/wake-up")
    public ResponseEntity<?> wakeUpParticipant(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId,
            @Parameter(description = "참가자 ID") @PathVariable Long participantId
    ) {

        challengeService.wakeUpParticipant(userPrincipal, challengeId, participantId);
        return ResponseEntity.ok(CommonApiResponse.success("챌린지 참가자 깨우기 알림이 전송되었습니다."));
    }




}
