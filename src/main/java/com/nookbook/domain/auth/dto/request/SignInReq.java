package com.nookbook.domain.auth.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.Email;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignInReq {
    @Schema(type = "string", example = "string@aa.bb", description="계정 이메일 입니다.")
    @Email
    @NotNull(message = "이메일은 필수 입력 값입니다.")
    //@JsonProperty("email")
    private String email;

    @Schema(type = "string", example = "string", description="사용자의 고유 providerId 입니다.")
    @NotNull(message = "ProviderId는 필수 입력 값입니다.")
    //@JsonProperty("providerId")
    private String providerId;

}