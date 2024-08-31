package com.nookbook.domain.note.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteListRes {

    @Schema(type = "Long", example = "1", description = "노트의 id")
    private Long noteId;

    @Schema(type = "String", example = "몰입이란 몰까...", description = "독서 기록의 제목")
    private String title;

    @Schema(type = "String", example = "2024-08-30", description = "독서 기록의 작성일")
    private String createdDate;
}
