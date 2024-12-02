package com.nookbook.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignInReq {

    @Schema(type = "string", example = "string@aa.bb", description = "계정 이메일입니다.")
    @JsonProperty("email")
    private String email;

    @Schema(type = "string", example = "ya29.a0AfH6SMC...", description = "Google Access Token입니다.")
    @JsonProperty("accessToken")
    private String accessToken;
}
