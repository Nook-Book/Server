package com.nookbook.domain.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChallengeParticipantListRes {
    @Schema(type = "List", description = "챌린지 참여자 목록")
    private List<ChallengeParticipantDetailRes> participantList;
}
