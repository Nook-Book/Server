package com.nookbook.domain.user.presentation;

import com.nookbook.domain.book.applicaiton.BookService;
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
@RequestMapping("/api/v1/user")
public class MyPageController {

    private final UserService userService;
    private final BookService bookService;

    @Operation(summary = "[마이페이지] 사용자 아이디 변경", description = "사용자의 아이디를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/{userId}/nickname-id")
    public ResponseEntity<?> updateNicknameId(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "user의 고유한 userId", required = true) @PathVariable Long userId,
            @Parameter(description = "변경할 아이디 입력값", required = true) @Valid @RequestBody NicknameIdCheckReq nicknameIdCheckReq
    ) {
        return userService.updateNicknameId(userPrincipal, userId, nicknameIdCheckReq);
    }

    @Operation(summary = "[마이페이지] 사용자 닉네임 변경", description = "사용자의 닉네임을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/{userId}/nickname")
    public ResponseEntity<?> updateNickname(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "user의 고유한 userId", required = true) @PathVariable Long userId,
            @Parameter(description = "변경할 닉네임 입력값", required = true) @Valid @RequestBody NicknameCheckReq nicknameCheckReq
    ) {
        return userService.updateNickname(userPrincipal, userId, nicknameCheckReq);
    }

    @Operation(summary = "[마이페이지] 사용자 프로필 이미지 변경", description = "사용자의 프로필 이미지를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/{userId}/image")
    public ResponseEntity<?> updateImage(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "user의 고유한 userId", required = true) @PathVariable Long userId,
            @Parameter(description = "기본 이미지 사용 여부", required = true) @RequestPart Boolean isDefaultImage,
            @Parameter(description = "변경할 프로필 이미지 파일") @RequestPart Optional<MultipartFile> image
    ) {
        return userService.updateImage(userPrincipal, userId, isDefaultImage, image);
    }

}