package com.nookbook.domain.collection.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CollectionBooksListRes {

    @Schema(type = "Long", example = "15", description = "총 도서 수")
    private Long totalBooks;

    @Schema(type = "List", description = "컬렉션 도서 리스트")
    private List< CollectionBooksListDetailRes > collectionBooksListDetailRes;

}
