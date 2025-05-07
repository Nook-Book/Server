package com.nookbook.domain.collection.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CollectionBooksListDetailRes {
    @Schema(type = "Long", example = "1", description = "도서 ID")
    private Long bookId;

    @Schema(type = "String", example = "24347", description = "도서의 isbn 정보")
    private String isbn;

    @Schema(type = "String", example = "조이의 첫 도서", description = "도서 제목")
    private String title;

    @Schema(type = "String", example = "김조이", description = "작가명")
    private String author;

    @Schema(type = "String", example = "조이의 첫 도서표지.png", description = "도서 표지 이미지")
    private String cover;
}
