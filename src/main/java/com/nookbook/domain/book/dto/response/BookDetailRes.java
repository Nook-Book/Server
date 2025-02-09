package com.nookbook.domain.book.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nookbook.domain.user_book.domain.BookStatus;
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
public class BookDetailRes {

    @JsonProperty("title")
    @Schema(type = "String", example = "몰입 : 인생을 바꾸는 자기 혁명 - Think Hard!", description = "도서의 제목")
    private String title;

    @JsonProperty("author")
    @Schema(type = "String", example = "황농문 (지은이)", description = "도서의 저자")
    private String author;

    @JsonProperty("cover")
    @Schema(type = "String", example = "https://image.aladin.co.kr/product/102/9/coversum/s552832633_2.jpg", description = "도서의 이미지")
    private String cover;

    @JsonProperty("isbn13")
    @Schema(type = "String", example = "9788925514826", description = "도서의 isbn 13자리")
    private String isbn13;

    @JsonProperty("pubDate")
    @Schema(type = "String", example = "2023-03-02", description = "도서의 출판일자")
    private String pubDate;

    // 페이지
    @JsonProperty("itemPage")
    @Schema(type = "int", example = "292", description = "도서의 총 페이지 수")
    private int page;

    @JsonProperty("fullDescription")
    @Schema(type = "String", example = "<BR>\\n<BR>\\n몰입전문가 황농문 교수는 바로 '몰입'이 천재성을 일깨워줄 열쇠라고 말한다. 저자는...", description = "도서의 상세 정보")
    private String description;

    // 목차
    @JsonProperty("toc")
    @Schema(type = "String", example = "<p>prologue 몰입, 최고의 나를 만나는 기회<BR>\\nintro 몰입 상태에서 경험한 문제 해결의 순간<BR>...", description = "도서의 목차")
    private String toc;

    @JsonProperty("link")
    @Schema(type = "String", example = "http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=1020939&amp;partner=openAPI&amp;start=api", description = "'자세히 보기' 클릭 시 연결되는 알라딘 도서 페이지 링크")
    private String link;

    @JsonProperty("categoryName")
    @Schema(type = "String", example = "소설/시/희곡", description = "도서의 카테고리입니다.")
    private String category;

    @JsonProperty("publisher")
    @Schema(type = "String", example = "데이원", description = "도서의 출판사")
    private String publisher;


    public void setToc(String toc) {
        this.toc = toc;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void formatCategoryName(String categoryName) {
        String[] parts = categoryName.split(">");
        if (parts.length > 1) {
            this.category = parts[1];
        } else {
            this.category = "기타";
        }
    }
}
