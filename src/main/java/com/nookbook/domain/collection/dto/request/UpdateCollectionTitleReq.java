package com.nookbook.domain.collection.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateCollectionTitleReq {
    @Schema(type = "String", example = "조이의 럭키비키 컬렉션", description = "컬렉션 제목")
    private String title;
}
