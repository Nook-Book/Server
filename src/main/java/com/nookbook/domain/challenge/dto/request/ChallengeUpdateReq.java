package com.nookbook.domain.challenge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChallengeUpdateReq {
    @Schema(type = "String", example = "밍쥬피쉬의 미라클 모닝 독서 챌린지", description = "챌린지 제목")
    private String title;
    @Schema(type = "String", example = "2024-09-01", description = "시작일")
    private String startDate;
    @Schema(type = "String", example = "2024-12-15", description = "종료일")
    private String endDate;
    @Schema(type = "int", example = "75", description = "일일 목표 독서 시간 (분 단위)")
    private Integer dailyGoal;
    @Schema(type = "String", example = "09:00", description = "일일 목표 시작 시간")
    private String startTime;
    @Schema(type = "String", example = "21:00", description = "일일 목표 종료 시간")
    private String endTime;
}
