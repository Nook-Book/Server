package com.nookbook.domain.collection.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CollectionBooksListDetailRes {
    private Long bookId;
    private String title;
    private String cover;
}
