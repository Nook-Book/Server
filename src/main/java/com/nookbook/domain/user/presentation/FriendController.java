package com.nookbook.domain.user.presentation;

import com.nookbook.domain.user.application.FriendService;
import com.nookbook.domain.user.dto.request.FriendRequestDecisionReq;
import com.nookbook.domain.user.dto.request.FriendRequestReq;
import com.nookbook.domain.user.dto.response.FriendsRequestRes;
import com.nookbook.domain.user.dto.response.SearchUserRes;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ErrorResponse;
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

import java.util.Optional;

@Tag(name = "Friend", description = "Friend API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 목록 조회 및 검색", description = "친구 목록을 조회 및 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SearchUserRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("")
    public ResponseEntity<?> getFriends(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "검색하고자 하는 단어를 입력해주세요.") @RequestParam(required = false) Optional<String> keyword
    ) {
        return friendService.getFriends(userPrincipal, keyword);
    }

    @Operation(summary = "친구 추가 - 내가 보낸/받은 요청 목록 조회", description = "내가 보낸/받은 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = FriendsRequestRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/requests")
    public ResponseEntity<?> getFriendsRequest(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return friendService.getFriendRequestList(userPrincipal);
    }

    @Operation(summary = "친구 요청", description = "다른 사용자에게 친구를 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class) ) } ),
            @ApiResponse(responseCode = "400", description = "저장 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/requests")
    public ResponseEntity<?> sendFriendRequest(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "Schemas의 FriendRequestReq를 참고해주세요.", required = true) @RequestBody FriendRequestReq friendRequestReq
            ) {
        return friendService.sendFriendRequest(userPrincipal, friendRequestReq);
    }

    @Operation(summary = "친구 수락/거절", description = "요청을 수락하거나 거절합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class) ) } ),
            @ApiResponse(responseCode = "400", description = "저장 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PatchMapping("/requests/{friendId}")
    public ResponseEntity<?> acceptOrRejectFriendRequest(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "친구 요청 목록에서 조회한 friendId를 입력해주세요.", required = true) @PathVariable Long friendId,
            @Parameter(description = "Schemas의 FriendRequestDecisionReq를 참고해주세요.", required = true) @RequestBody FriendRequestDecisionReq decisionReq
            ) {
        return friendService.updateFriendRequestStatus(userPrincipal, friendId, decisionReq);
    }

    @Operation(summary = "친구 요청 취소", description = "내가 보낸 친구 요청을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "취소 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class) ) } ),
            @ApiResponse(responseCode = "400", description = "취소 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @DeleteMapping ("/requests/{friendId}")
    public ResponseEntity<?> cancelFriendRequest(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "친구 요청 목록에서 조회한 friendId를 입력해주세요.", required = true) @PathVariable Long friendId
    ) {
        return friendService.deleteFriendRequest(userPrincipal, friendId, false);
    }

    @Operation(summary = "친구 삭제", description = "친구를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class) ) } ),
            @ApiResponse(responseCode = "400", description = "삭제 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @DeleteMapping ("/{friendId}")
    public ResponseEntity<?> deleteFriendRequest(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "친구 목록에서 조회한 friendId를 입력해주세요.", required = true) @PathVariable Long friendId
    ) {
        return friendService.deleteFriendRequest(userPrincipal, friendId, true);
    }

}
