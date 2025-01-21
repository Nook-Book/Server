package com.nookbook.domain.challenge.dto.response;

import com.nookbook.domain.challenge.domain.ChallengeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChallengeDetailRes {

    @Schema(type = "Long", example = "1", description = "챌린지 ID")
    private Long challengeId;

    @Schema(type = "Boolean", example = "true", description = "챌린지 수정 권한")
    private Boolean isEditable;

    @Schema(type = "String", example = "밍쥬피쉬의 미라클 모닝 독서 챌린지", description = "챌린지 제목")
    private String title;

    @Schema(type = "String", example = "챌린지커버이미지.jpg", description = "챌린지 커버 이미지")
    private String challengeCover;

    @Schema(type = "String", example = "WAITING / PROGRESS / END", description = "챌린지 상태")
    private ChallengeStatus challengeStatus;

    @Schema(type = "String", example = "2021-08-01", description = "챌린지 시작일")
    private LocalDate startDate;

    @Schema(type = "String", example = "2021-08-31", description = "챌린지 종료일")
    private LocalDate endDate;

    @Schema(type = "Integer", example = "16", description = "총 시간 (시간 단위)")
    private int totalHour;

    @Schema(type = "Integer", example = "75", description = "일일 목표 독서 시간 (분 단위)")
    private Integer dailyGoal; // 선택적 필드이므로 null을 허용하는 Integer 타입으로 변경

    @Schema(type = "LocalTime", example = "08:00", description = "일일 독서 시작 시간")
    private LocalTime dailyStartTime;

    @Schema(type = "LocalTime", example = "22:00", description = "일일 독서 종료 시간")
    private LocalTime dailyEndTime;

    @Schema(type = "int", description = "참여자 목록")
    private List<ParticipantStatusListRes> participants;
}
