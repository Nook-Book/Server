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

    @Schema(type = "Long", example = "1", description = "순서")
    private Long order;
}
