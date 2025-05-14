package com.nookbook.domain.timer.presentation;

import com.nookbook.domain.timer.application.TimerService;
import com.nookbook.domain.timer.dto.request.UpdateTimerReq;
import com.nookbook.domain.timer.dto.response.TimerRes;
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

@Tag(name = "Timer", description = "타이머 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TimerController {

    private final TimerService timerService;

    @Operation(summary = "타이머 기록 조회", description = "타이머 기록의 누적 시간 및 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TimerRes.class)) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @GetMapping("/books/{bookId}/timers")
    public ResponseEntity<?> getTimerRecord(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "도서의 id를 입력해주세요.", required = true) @PathVariable Long bookId
    ) {
        return timerService.getTimerRecords(userPrincipal, bookId);
    }

    @Operation(summary = "타이머 종료", description = "타이머를 종료하고, 타이머 기록을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TimerRes.class)) } ),
            @ApiResponse(responseCode = "400", description = "저장 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @PatchMapping("/timers/{timerId}")
    public ResponseEntity<?> endTimerRecord(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "종료하려는 타이머의 id를 입력해주세요.", required = true) @PathVariable Long timerId,
            @Parameter(description = "시간을 입력해주세요.", required = true) @RequestBody UpdateTimerReq updateTimerReq
    ) {
        return timerService.updateTimer(userPrincipal, timerId, updateTimerReq);
    }

    @Operation(summary = "타이머 시작", description = "타이머를 시작합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) } ),
            @ApiResponse(responseCode = "400", description = "저장 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @PostMapping("/books/{bookId}/timers")
    public ResponseEntity<?> startTimerRecord(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "도서의 id를 입력해주세요.", required = true) @PathVariable Long bookId
    ) {
        return timerService.createTimer(userPrincipal, bookId);
    }
}
