package com.nookbook.domain.timer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class CreateTimerReq {

    @Schema(type = "BigInteger", example = "48000", description = "특정 도서의 타이머 독서 시간입니다. 초단위로 전달해주세요.")
    private BigInteger time;
}
