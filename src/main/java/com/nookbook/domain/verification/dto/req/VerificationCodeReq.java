package com.nookbook.domain.verification.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VerificationCodeReq {

    @Schema(type = "string", example = "user@example.com", description = "인증 코드를 받은 이메일 주소")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Schema(type = "string", example = "A3B7K9", description = "6자리 인증 코드")
    @NotBlank(message = "인증 코드는 필수 입력값입니다.")
    private String code;
}
