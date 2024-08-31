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

    @Schema(type = "String", example = "조이의 첫 도서", description = "도서 제목")
    private String title;

    @Schema(type = "String", example = "Zoey", description = "도서 저자")
    private String cover;
}
