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
public class ChallengeParticipantDetailRes {
    @Schema(type = "Long", example = "1", description = "참여자 ID")
    private Long participantUserId;

    @Schema(type = "String", example = "참여자 닉네임", description = "��여자 닉네임")
    private String participantNickname;

    @Schema(type = "String", example = "참여자 프로필 이미지", description = "참여자 프로필 이미지")
    private String participantProfileImage;

    @Schema(type = "Boolean", example = "true", description = "방장 여부")
    private Boolean isOwner;

}
