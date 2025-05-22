package com.nookbook.domain.collection.dto.request;

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
public class BookIdListReq {
    @Schema(type = "List<Long>", example = "[1, 2, 3]", description = "이동할 도서의 ID 리스트")
    private List<Long> bookIds;
}
