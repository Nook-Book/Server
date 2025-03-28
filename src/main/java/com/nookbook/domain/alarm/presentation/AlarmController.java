package com.nookbook.domain.alarm.presentation;

import com.nookbook.domain.alarm.application.AlarmService;
import com.nookbook.domain.alarm.dto.response.AlarmListRes;
import com.nookbook.domain.alarm.dto.response.AlarmRes;
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
@RequestMapping("/api/v1/alarm")
@Tag(name = "Alarm", description = "알림 관련 API입니다.")
public class AlarmController {
    private final AlarmService alarmService;

    @Operation(summary = "알림 목록 조회 API", description = "알림 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AlarmListRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "알림 목록 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping()
    public ResponseEntity<?> getAllAlarms(
            @Parameter @CurrentUser UserPrincipal userPrincipal

    ) {
        return alarmService.getAllAlarms(userPrincipal);
    }
}
