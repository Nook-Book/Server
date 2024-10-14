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
public class ChallengeListDetailRes {

    @Schema(type = "Long", example = "1", description = "챌린지 ID")
    private Long challengeId;

    @Schema(type = "String", example = "밍쥬피쉬의 미라클 모닝 독서 챌린지", description = "챌린지 제목")
    private String title;

    @Schema(type = "String", example = "밍쥬피쉬증명사진.jpg", description = "챌린지 커버 이미지")
    private String challengeCover;

}
