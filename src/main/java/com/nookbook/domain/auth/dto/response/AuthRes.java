package com.nookbook.domain.auth.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthRes {

    @Schema(type = "string", example = "ya29.a0AfH6SMC...", description = "Google Access Token입니다.")
    @JsonProperty("accessToken")
    private String accessToken;

    @Schema(type = "string", example = "string", description = "Refresh Token입니다.")
    @JsonProperty("refreshToken")
    private String refreshToken;

    @Schema(type = "string", example = "string@aa.bb", description = "유저 이메일입니다.")
    @JsonProperty("email")
    private String email;
}