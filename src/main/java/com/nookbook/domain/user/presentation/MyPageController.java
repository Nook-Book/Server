package com.nookbook.domain.user.presentation;

import com.nookbook.domain.book.applicaiton.BookService;
import com.nookbook.domain.book.dto.response.BookStatisticsRes;
import com.nookbook.domain.book.dto.response.MostReadCategoriesRes;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.dto.request.NicknameCheckReq;
import com.nookbook.domain.user.dto.request.NicknameIdCheckReq;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Tag(name = "User MyPage", description = "User MyPage API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyPageController {

    private final UserService userService;
    private final BookService bookService;

    @Operation(summary = "[마이페이지] 사용자 아이디 변경", description = "사용자의 아이디를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/nickname-id")
    public ResponseEntity<?> updateNicknameId(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "변경할 아이디 입력값", required = true) @Valid @RequestBody NicknameIdCheckReq nicknameIdCheckReq
    ) {
        return userService.updateNicknameId(userPrincipal, nicknameIdCheckReq);
    }

    @Operation(summary = "[마이페이지] 사용자 닉네임 변경", description = "사용자의 닉네임을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/nickname")
    public ResponseEntity<?> updateNickname(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "변경할 닉네임 입력값", required = true) @Valid @RequestBody NicknameCheckReq nicknameCheckReq
    ) {
        return userService.updateNickname(userPrincipal, nicknameCheckReq);
    }

    @Operation(summary = "[마이페이지] 사용자 프로필 이미지 변경", description = "사용자의 프로필 이미지를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/image")
    public ResponseEntity<?> updateImage(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "기본 이미지 사용 여부", required = true) @RequestPart Boolean isDefaultImage,
            @Parameter(description = "변경할 프로필 이미지 파일") @RequestPart Optional<MultipartFile> image
    ) {
        return userService.updateImage(userPrincipal, isDefaultImage, image);
    }

    @Operation(summary = "[마이페이지] 독서 통계(카테고리별)", description = "마이페이지의 독서 통계(카테고리)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MostReadCategoriesRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/report/category")
    public ResponseEntity<?> findReadingReportByCategory(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return bookService.countReadBooksByCategory(userPrincipal);
    }

    @Operation(summary = "[마이페이지] 독서 통계(연도 및 월별)", description = "마이페이지의 독서 통계(연도 및 월별)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BookStatisticsRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/report")
    public ResponseEntity<?> findReadingReportByYear(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "독서 통계를 확인하려는 연도를 입력해주세요", required = true) @RequestParam int year
    ) {
        return bookService.countReadBooksByYear(userPrincipal, year);
    }
}
