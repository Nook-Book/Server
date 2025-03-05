package com.nookbook.domain.user_book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MonthlyUserBookCalendarRes {
    @Schema(type = "String", example = "2021-08-07", description = "날짜")
    private String date;

    @Schema(type = "object", description = "일별 독서 정보")
    private DailyUserBookCalendarRes dailyUserBookCalendar;
}
