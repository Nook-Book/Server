package com.nookbook.domain.book.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BestSellerBookRes {

    @JsonProperty("title")
    @Schema(type = "String", example = "세이노의 가르침 (100만 부 한정판)", description = "도서의 제목")
    private String title;

    @JsonProperty("author")
    @Schema(type = "String", example = "세이노(SayNo) (지은이)", description = "도서의 저자")
    private String author;

    @JsonProperty("isbn13")
    @Schema(type = "String", example = "9791168473690", description = "도서의 isbn 13자리")
    private String isbn13;

    @JsonProperty("cover")
    @Schema(type = "String", example = "https://image.aladin.co.kr/product/34366/29/coversum/k792932002_1.jpg", description = "도서의 이미지")
    private String cover;

    @JsonProperty("bestRank")
    @Schema(type = "int", example = "1", description = "도서의 베스트셀러 순위")
    private int bestRank;
}