package com.nookbook.domain.verification.presentation;

import com.nookbook.domain.mail.dto.req.SendCodeReq;
import com.nookbook.domain.verification.application.VerificationService;
import com.nookbook.domain.verification.dto.req.VerificationCodeReq;
import com.nookbook.global.payload.ErrorResponse;
import com.nookbook.global.payload.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Verification", description = "이메일 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/verification")
public class VerificationController {

    private final VerificationService verificationService;

    @Operation(summary = "인증 코드 발송 API", description = "이메일로 인증 코드를 발송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 코드 발송 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
            @ApiResponse(responseCode = "500", description = "이메일 전송 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/code")
    public ResponseEntity<Message> sendVerificationCode(@Valid @RequestBody SendCodeReq sendCodeReq) {
        return verificationService.sendVerificationCode(sendCodeReq);
    }

    @Operation(summary = "인증 코드 확인 API", description = "이메일로 발송된 인증 코드를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "인증 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/code/check")
    public ResponseEntity<Message> verifyCode(@Valid @RequestBody VerificationCodeReq verificationCodeReq) {
        return verificationService.verifyCode(verificationCodeReq);
    }
}
