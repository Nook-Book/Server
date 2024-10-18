package com.nookbook.domain.note.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateNoteReq {

    @Schema(type = "Long", example = "1", description = "도서의 id")
    private Long bookId;

    @Schema(type = "String", example = "몰입이란 몰까...", description = "독서 기록의 제목")
    private String title;

    @Schema(type = "String", example = "책에서 말하길 몰입을 잘하려면 첫번째로", description = "독서 기록의 내용")
    private String content;
}
