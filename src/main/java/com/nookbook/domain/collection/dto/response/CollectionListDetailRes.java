package com.nookbook.domain.collection.dto.response;

import com.nookbook.domain.collection.domain.CollectionStatus;
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
public class CollectionListDetailRes {

    @Schema(type = "Long", example = "1", description = "컬렉션 순서")
    private Long order;

    @Schema(type = "CollectionStatus", example = "MAIN, NOMAL", description = "컬렉션 상태")
    private CollectionStatus collectionStatus;

    @Schema(type = "Long", example = "1", description = "컬렉션 ID")
    private Long collectionId;

    @Schema(type = "String", example = "읽고 싶은", description = "컬렉션 제목")
    private String collectionTitle;

    @Schema(type = "int", example = "3", description = "컬렉션 내 도서 수")
    private int totalBooks;

    @Schema(type = "List", description = "컬렉션에 속한 책 표지 이미지 목록")
    private List<String> collectionBooksCoverList;

}
