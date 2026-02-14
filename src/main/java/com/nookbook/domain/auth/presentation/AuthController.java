package com.nookbook.domain.auth.presentation;

import com.nookbook.domain.auth.application.AuthService;
import com.nookbook.domain.auth.dto.request.LocalSignInReq;
import com.nookbook.domain.auth.dto.request.SignInReq;
import com.nookbook.domain.auth.dto.request.SignUpReq;
import com.nookbook.domain.auth.dto.response.LoginResponse;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ErrorCode;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "카카오 로그인 API", description = "카카오 로그인 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 및 토큰 발급 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class) ) } ),
            @ApiResponse(responseCode = "400", description = "로그인 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/login")
    public ResponseEntity<?> idTokenLogin(@RequestBody SignInReq signinReq) {
        String accessToken = signinReq.getAccessToken();
        String email = signinReq.getEmail();

        log.info("accessToken: {}", accessToken);
        log.info("email: {}", email);

        if (accessToken == null || accessToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID 토큰은 null이거나 비어 있을 수 없습니다");
        }
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("이메일은 null이거나 비어 있을 수 없습니다");
        }

        try {
            return authService.loginWithIdToken(accessToken, email);
        } catch (RuntimeException e) {
            ErrorCode errorCode = ErrorCode.INVALID_TOKEN;
            ErrorResponse errorResponse = ErrorResponse.of(errorCode, e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(summary = "자체 회원가입 API", description = "이메일과 비밀번호로 회원가입합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "회원가입 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpReq signUpReq) {
        try {
            return authService.signUp(signUpReq.getEmail(), signUpReq.getPassword(), signUpReq.getNickname());
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.INVALID_PARAMETER;
            ErrorResponse errorResponse = ErrorResponse.of(errorCode, e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(summary = "자체 로그인 API", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class) ) } ),
            @ApiResponse(responseCode = "400", description = "로그인 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/login/local")
    public ResponseEntity<?> localLogin(@Valid @RequestBody LocalSignInReq localSignInReq) {
        try {
            return authService.localLogin(localSignInReq.getEmail(), localSignInReq.getPassword());
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.INVALID_PARAMETER;
            ErrorResponse errorResponse = ErrorResponse.of(errorCode, e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "로그아웃 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Parameter @CurrentUser UserPrincipal userPrincipal) {
        return authService.logout(userPrincipal);
    }

    @Operation(summary = "탈퇴하기 API", description = "탈퇴하기 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "탈퇴하기 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "탈퇴하기 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @DeleteMapping("/exit")
    public ResponseEntity<?> exit(@Parameter @CurrentUser UserPrincipal userPrincipal) {
        return authService.exit(userPrincipal);
    }

}