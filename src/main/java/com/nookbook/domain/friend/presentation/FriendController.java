package com.nookbook.domain.friend.presentation;

import com.nookbook.domain.friend.application.FriendService;
import com.nookbook.domain.friend.dto.response.FriendListRes;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friend")
@Tag(name = "Friend", description = "친구 관련 API입니다.")
public class FriendController {
    private final FriendService friendService;


    @Operation(summary = "친구 목록 조회 API", description = "유저의 친구 목록을 조회하는 API입니다.")
    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = FriendListRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "친구 목록 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    public ResponseEntity<?> getFriendList(
            @Parameter @CurrentUser UserPrincipal userPrincipal
    ) {
        return friendService.getFriendList(userPrincipal);
    }

}
