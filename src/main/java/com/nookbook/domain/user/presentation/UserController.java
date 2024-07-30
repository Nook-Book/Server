package com.nookbook.domain.user.presentation;

import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.dto.request.NicknameIdCheckReq;
import com.nookbook.domain.user.dto.request.NicknameCheckReq;
import com.nookbook.domain.user.dto.request.UserInfoReq;
import com.nookbook.domain.user.dto.response.NicknameCheckRes;
import com.nookbook.domain.user.dto.response.NicknameIdCheckRes;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "User API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 정보 등록", description = "사용자가 설정한 정보를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 등록 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "사용자 정보 등록 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/info")
    public ResponseEntity<?> saveUserInfo(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "유저 닉네임/아이디", required = true)
            @RequestBody UserInfoReq userInfoReq
    ) {
        return userService.saveUserInfo(userPrincipal, userInfoReq);
    }


    @Operation(summary = "사용자 아이디 중복 확인", description = "사용자가 입력한 아이디가 중복인지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능한 아이디", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = NicknameIdCheckRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "사용 불가한 아이디", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/nickname-id")
    public ResponseEntity<?> checkNicknameId(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "중복 여부를 확인할 아이디 입력값", required = true)
            @RequestBody NicknameIdCheckReq nicknameIdCheckReq
    ) {
        return userService.checkNicknameId(userPrincipal, nicknameIdCheckReq);
    }


    @Operation(summary = "사용자 닉네임 중복 확인", description = "사용자가 입력한 닉네임이 중복인지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = NicknameCheckRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "사용 불가한 닉네임", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/nickname")
    public ResponseEntity<?> checkNickname(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "중복 여부를 확인할 닉네임 입력값", required = true)
            @RequestBody NicknameCheckReq nicknameCheckReq
    ) {
        return userService.checkNickname(userPrincipal, nicknameCheckReq);
    }

}
