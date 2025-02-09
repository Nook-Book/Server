package com.nookbook.domain.timer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimerRes {

    @Schema(type = "boolean", example = "true", description = "타이머 상태입니다. true: 타이머를 켜고 책을 읽는 중, false: 타이머를 끔(읽고있지 않음)")
    @JsonProperty("isReading")
    private boolean reading;

    @Schema(type = "Long", example = "1", description = "타이머를 켜둔 상태라면 현재 타이머의 id를 반환합니다. 타이머가 꺼져있다면 전달되지 않습니다.")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long timerId;

    @Schema(type = "String", example = "01:20:00", description = "누적 독서 시간입니다. HH:MM:SS 형식입니다.")
    private String totalReadTime;

    @Schema(type = "array", description = "Schemas의 TimerRecordRes를 참고해주세요. 타이머를 이용해 기록된 독서 시간의 리스트입니다.")
    private List<TimerRecordRes> recordResList;

}
