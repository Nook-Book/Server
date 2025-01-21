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
public class ParticipantListRes {
    // 사용자 본인의 방장 여부
    @Schema(type = "Boolean", example = "true", description = "사용자 본인의 방장 여부 / 방장이면 true, 일반 참여자면 false")
    private Boolean isOwner;

    @Schema(type = "array", description = "참여자 정보 목록")
    private List<ParticipantRes> participantList;
}
