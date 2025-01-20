package com.nookbook.domain.timer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimerRecordRes {

    @Schema(type = "Long", example = "1", description = "독서 시간의 id")
    public Long timerId;

    @Schema(type = "LocalDate", example = "2025-01-21", description = "타이머를 시작한 일시입니다.")
    public LocalDate date;

    @Schema(type = "String", example = "01:20:00", description = "독서 시간입니다. HH:MM:SS 형식입니다.")
    public String readTime;
}
