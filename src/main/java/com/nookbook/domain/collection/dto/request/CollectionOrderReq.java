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
public class CollectionOrderReq {

    @Schema(type = "Long", example = "1", description = "컬렉션 ID")
    private Long collectionId;

    @Schema(type = "int", example = "1", description = "컬렉션 상태 (1 : 현재 컬렉션, 0 : 전체 컬렉션)")
    private int status;

    @Schema(type = "Long", example = "1", description = "순서")
    private Long order;
}
