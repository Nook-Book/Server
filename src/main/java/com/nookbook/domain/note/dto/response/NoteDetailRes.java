package com.nookbook.domain.note.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NoteDetailRes {

    @Schema(type = "String", example = "몰입이란 몰까...", description = "독서 기록의 제목")
    private String title;

    @Schema(type = "String", example = "책에서 말하길 몰입을 잘하려면 첫번째로", description = "독서 기록의 내용")
    private String content;

    @Schema(type = "String", example = "2024-08-30", description = "독서 기록의 작성일")
    private String createdDate;

    @Builder
    public NoteDetailRes(String title, String content, String createdDate) {
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
    }
}
