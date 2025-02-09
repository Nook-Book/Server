package com.nookbook.domain.note.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherUserNoteListRes {

    @Schema(type = "Long", example = "1", description = "도서의 bookId")
    private Long bookId;

    @Schema(type = "String", example = "몰입 : 인생을 바꾸는 자기 혁명 - Think Hard!", description = "도서의 제목")
    private String title;

    @Schema(type = "String", example = "https://image.aladin.co.kr/product/102/9/coversum/s552832633_2.jpg", description = "도서의 이미지")
    private String cover;

    @Schema(type = "String", example = "황농문 (지은이)", description = "도서의 저자")
    private String author;

    @Schema(type = "String", example = "데이원", description = "도서의 출판사")
    private String publisher;
}
