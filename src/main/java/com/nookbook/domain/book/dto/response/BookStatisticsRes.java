package com.nookbook.domain.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookStatisticsRes {

    @Schema(type = "int", example = "12", description = "특정 연도의 month(월)입니다.")
    private int month;

    @Schema(type = "int", example = "12", description = "책을 몇 권 읽었는지 센 수입니다.")
    private int count;

    @Builder
    public BookStatisticsRes(int month, int count) {
        this.month = month;
        this.count = count;
    }
}
