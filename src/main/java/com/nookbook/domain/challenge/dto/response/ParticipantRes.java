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
public class ParticipantRes {
    @Schema(type = "Long", example = "1", description = "사용자 ID")
    private Long userId;

    @Schema(type = "Long", example = "1", description = "참여자 ID")
    private Long participantId;

    @Schema(type = "String", example = "피쉬벅", description = "참여자 닉네임")
    private String participantNickname;

    @Schema(type = "String", example = "피쉬벅증명사진.jpg", description = "참여자 프로필 이미지")
    private String participantImage;

    // 역할 (방장, 참여자)
    @Schema(type = "String", example = "방장", description = "참여자 역할 / 방장 or 일반")
    private String role;

}
