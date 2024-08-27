package com.nookbook.domain.book.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BestSellerRes {

    @Schema(type = "int", example = "172", description = "API의 총 결과수")
    private int totalResults;

    @Schema(type = "int", example = "1", description = "Page 수")
    private int startIndex;

    @Schema(type = "int", example = "10", description = "한 페이지에 출력될 상품 수")
    private int itemsPerPage;

    @JsonProperty("item")
    @Schema(type = "List<BestSellerBookRes>", example = "BestSellerBookRes의 Schemas를 참고해주세요.", description = "검색된 베스트셀러 정보의 리스트")
    private List<BestSellerBookRes> items;

}
