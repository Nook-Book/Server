package com.nookbook.domain.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeywordRes {

    @Schema(type = "Long", example = "1", description = "검색어의 고유 id")
    private Long keywordId;

    @Schema(type = "String", example = "세이노", description = "검색어")
    private String content;
}
