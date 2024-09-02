package com.nookbook.domain.challenge.presentation;

import com.nookbook.domain.challenge.application.ChallengeService;
import com.nookbook.domain.challenge.dto.request.ChallengeCreateReq;
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

}
