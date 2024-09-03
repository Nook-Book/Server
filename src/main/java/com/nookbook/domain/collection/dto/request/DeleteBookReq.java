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
public class DeleteBookReq {
    @Schema(type = "List", example = "[1,2,3,4]", description = "삭제할 도서 ID 리스트")
    private List<Long> bookIds;
}
