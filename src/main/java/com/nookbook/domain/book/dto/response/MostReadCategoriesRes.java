package com.nookbook.domain.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MostReadCategoriesRes {

    @Schema(type = "String", example = "소설/시/희곡", description = "도서의 카테고리입니다.")
    private String category;

    @Schema(type = "int", example = "12", description = "특정 카테고리를 몇 권 읽었는지 센 수입니다.")
    private int count;


    @Builder
    public MostReadCategoriesRes(String category, int count) {
        this.category = category;
        this.count = count;
    }

}
