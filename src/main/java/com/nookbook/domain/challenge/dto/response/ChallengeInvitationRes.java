package com.nookbook.domain.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChallengeInvitationRes {
    @Schema(type = "Long", example = "1", description = "친구의 userId")
    private Long userId;

    @Schema(type = "String", example = "야옹아멍멍해바", description = "친구의 닉네임")
    private String nickname;

    @Schema(type = "boolean", example = "true", description = "초대 가능 여부")
    private boolean isInvitable;
}
