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
public class CollectionListRes {
    @Schema(type = "Long", example = "17", description = "총 컬렉션 수")
    private Long totalCollections;

    @Schema(type = "List", description = "컬렉션 리스트")
    private List<CollectionListDetailRes> collectionListDetailRes;
}
