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
public class ChallengeListRes {

    @Schema(type = "Long", example = "2", description = "대기중인 챌린지 수")
    private int waitingCount;

    @Schema(type = "List", description = "대기중인 챌린지 목록")
    private List<ChallengeListDetailRes> waitingList;

    @Schema(type = "Long", example = "3", description = "진행중인 챌린지 수")
    private int progressCount;

    @Schema(type = "List", description = "진행중인 챌린지 목록")
    private List<ChallengeListDetailRes> progressList;

    @Schema(type = "Long", example = "1", description = "종료된 챌린지 수")
    private int endCount;

    @Schema(type = "List", description = "종료된 챌린지 목록")
    private List<ChallengeListDetailRes> endList;

}
