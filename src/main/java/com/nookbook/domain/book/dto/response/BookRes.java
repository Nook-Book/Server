package com.nookbook.domain.book.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nookbook.domain.user_book.domain.BookStatus;
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
public class BookRes {

    @Schema(type = "String", example = "BEFORE_READING", description = "도서의 읽음 상태. BEFORE_READING(읽기 전), READING(읽는 중)")
    private BookStatus bookStatus;

    @Schema(type = "boolean", example = "true", description = "도서의 컬렉션 저장 여부")
    private boolean storedCollection;

    @JsonProperty("item")
    @Schema(type = "BookDetailRes", example = "BookDetailRes의 Schemas를 참고해주세요.", description = "조회한 도서의 상세 정보")
    private BookDetailRes item;

    public void setBookStatus(BookStatus bookStatus) {
        this.bookStatus = bookStatus;
    }

    public void setStoredCollection(boolean storedCollection) {
        this.storedCollection = storedCollection;
    }

}
