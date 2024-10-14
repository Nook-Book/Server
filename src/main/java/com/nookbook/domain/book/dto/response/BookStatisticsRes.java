package com.nookbook.domain.book.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookStatisticsRes {

    private String month;

    private int count;

    @Builder
    public BookStatisticsRes(String month, int count) {
        this.month = month;
        this.count = count;
    }
}
