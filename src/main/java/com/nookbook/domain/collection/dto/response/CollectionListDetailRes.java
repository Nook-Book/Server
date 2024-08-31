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
    private Long id;

    @Schema(type = "String", example = "읽고 싶은", description = "컬렉션 제목")
    private String title;

    @Schema(type = "List", description = "최근 추가된 도서 top4에 해당하는 도서 표지 이미지 리스트")
    private List<String> coverList;
}
