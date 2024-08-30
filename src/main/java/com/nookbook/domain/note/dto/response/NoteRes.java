package com.nookbook.domain.note.dto.response;

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
public class NoteRes {

    @Schema(type = "String", example = "몰입 : 인생을 바꾸는 자기 혁명 - Think Hard!", description = "도서의 제목")
    private String bookTitle;

    @Schema(type = "String", example = "https://image.aladin.co.kr/product/102/9/coversum/s552832633_2.jpg", description = "도서의 이미지")
    private String bookImage;

    @Schema(type = "int", example = "3", description = "노트의 총 개수")
    private int noteCount;

    @Schema(type = "List", example = "Schemas의 NoteListRes를 참고해주세요", description = "노트의 목록")
    private List<NoteListRes> noteListRes;
}
