package com.nookbook.domain.user_book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DailyUserBookCalendarRes {
    // 총 독서 시간
    // HH:MM:SS
    @Schema(type = "String", example = "02:34:10", description = "총 독서 시간")
    private String totalReadTime;

    // 독서 시작 시간
    // HH:MM
    @Schema(type = "String", example = "21:13", description = "독서 시작 시간")
    private String startTime;

    // 독서 종료 시간
    // HH:MM
    @Schema(type = "String", example = "23:13", description = "독서 종료 시간")
    private String endTime;

    // 읽은 책 목록
    // 책 제목 - 책 이미지
    @Schema(type = "array", description = "읽은 책 목록")
    private List<Map<String, String>> bookList;
}
