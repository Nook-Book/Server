package com.nookbook.domain.alarm.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlarmListRes<T> {
    @ArraySchema(schema = @Schema(type = "array", description = "알림 목록입니다. AlarmRes를 확인해주세요.", implementation = AlarmRes.class))
    private List<T> alarmList;

}
