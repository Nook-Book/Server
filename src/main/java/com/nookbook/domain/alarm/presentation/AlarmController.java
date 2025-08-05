package com.nookbook.domain.alarm.presentation;

import com.nookbook.domain.alarm.application.AlarmService;
import com.nookbook.domain.alarm.dto.response.AlarmListRes;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarms")
@Tag(name = "Alarm", description = "알림 관련 API입니다.")
public class AlarmController {
    private final AlarmService alarmService;

    @Operation(summary = "알림 목록 조회 API", description = "알림 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AlarmListRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "알림 목록 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("")
    public ResponseEntity<?> getAllAlarms(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "page의 size입니다. 기본 값은 30입니다.", required = true) @RequestParam(defaultValue = "30") int size,
            @Parameter(description = "알림 목록을 페이지별로 조회합니다. **Page는 0부터 시작합니다!**", required = true) @RequestParam(defaultValue = "0") int page

    ) {
        return alarmService.getAllAlarms(userPrincipal, page, size);
    }

    // 일괄 읽음처리
    @Operation(summary = "알림 일괄 읽음 처리 API", description = "모든 알림을 읽음 상태로 변경하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 일괄 읽음 처리 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "알림 일괄 읽음 처리 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PatchMapping("")
    public ResponseEntity<Message> markAllAsRead(
            @Parameter @CurrentUser UserPrincipal userPrincipal
    ) {
        alarmService.markAllAsRead(userPrincipal);
        return ResponseEntity.ok(new Message("모든 알림이 읽음 처리되었습니다."));
    }
}
