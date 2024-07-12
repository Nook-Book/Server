package com.nookbook.domain.auth.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.Email;

@Data
public class SignInReq {

    @Schema( type = "string", example = "string@aa.bb", description="계정 이메일 입니다.")
    @Email
    private String email;

    @Schema( type = "string", example = "string", description="사용자의 고유 providerId 입니다.")
    private String providerId;

}