package com.nookbook.domain.user_book.presentation;

import com.nookbook.domain.user_book.application.UserBookService;
import com.nookbook.domain.user_book.dto.response.DailyUserBookCalendarRes;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-book")
@Tag(name = "UserBook", description = "사용자 도서 관련 API입니다.")
public class UserBookController {

    private final UserBookService userBookService;

    // 날짜별 사용자 독서 기록 조회
    // 날짜 형식: 2021-11-01 또는 2021-11
    @Operation(summary = "날짜별 사용자 독서 기록 조회", description = "사용자의 날짜별 독서 기록을 조회합니다.")
    @GetMapping("/calendar/{date}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "독서 캘린더 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = DailyUserBookCalendarRes.class)) } ),
            @ApiResponse(responseCode = "400", description = "독서 캘린더 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    public ResponseEntity<?> getUserBookCalendar(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "조회할 날짜를 입력해주세요.", example = "2021-11-01", required = true) @PathVariable String date
    ) {
        return userBookService.getUserBookCalendar(userPrincipal, date);
    }
}
