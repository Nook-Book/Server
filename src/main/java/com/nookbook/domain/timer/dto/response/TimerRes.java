package com.nookbook.domain.timer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimerRes {

    @Schema(type = "String", example = "01:20:00", description = "독서 시간입니다. HH:MM:SS 형식입니다.")
    public String totalReadTime;

    @Schema(type = "array", description = "Schemas의 TimerRecordRes를 참고해주세요. 타이머를 이용해 기록된 독서 시간의 리스트입니다.")
    private List<TimerRecordRes> recordResList;

}
