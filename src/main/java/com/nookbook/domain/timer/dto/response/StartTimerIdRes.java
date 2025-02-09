package com.nookbook.domain.timer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartTimerIdRes {

    @Schema(type = "Long", example = "1", description = "타이머의 id입니다.")
    private Long timerId;
}
