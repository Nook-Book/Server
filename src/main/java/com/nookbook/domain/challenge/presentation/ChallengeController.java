package com.nookbook.domain.challenge.presentation;

import com.nookbook.domain.challenge.application.ChallengeService;
import com.nookbook.domain.challenge.dto.request.ChallengeCreateReq;
import com.nookbook.domain.challenge.dto.response.ChallengeDetailRes;
import com.nookbook.domain.challenge.dto.response.ChallengeListRes;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
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
@RequestMapping("/api/v1/challenge")
@Tag(name = "Challenge", description = "챌린지 관련 API입니다.")
public class ChallengeController {

    private final ChallengeService challengeService;

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
    @GetMapping("/list")
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
        return challengeService.getChallengeDetail(userPrincipal, challengeId);
    }

    // 챌린지 참가자 삭제 API
    @Operation(summary = "챌린지 참가자 삭제 API", description = "챌린지 참가자를 삭제하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 참가자 삭제 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 참가자 삭제 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping("/{challengeId}/participant/{participantId}")
    public ResponseEntity<?> deleteParticipant(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId,
            @Parameter(description = "참가자 ID") @PathVariable Long participantId
    ) {
        return challengeService.deleteParticipant(userPrincipal, challengeId, participantId);
    }

    // 챌린지 참가자 추가 API
    @Operation(summary = "챌린지 참가자 추가 API", description = "챌린지 참가자를 추가하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 참가자 추가 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "챌린지 참가자 추가 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/{challengeId}/participant/{participantId}")
    public ResponseEntity<?> addParticipant(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "챌린지 ID") @PathVariable Long challengeId,
            @Parameter(description = "참가자 ID") @PathVariable Long participantId
    ) {
        return challengeService.addParticipant(userPrincipal, challengeId, participantId);
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



}
