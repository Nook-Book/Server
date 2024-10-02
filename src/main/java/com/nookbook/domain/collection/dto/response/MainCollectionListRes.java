package com.nookbook.domain.collection.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MainCollectionListRes {

    @Schema(type = "int", example = "3", description = "현재 컬렉션 수(1개 이상 4개 이하)")
    private int totalCollections;

    @Schema(type = "List", example = "[1,3,1]", description = "컬렉션 ID 리스트 (정렬된 상태)")
    private ArrayList<Long> collectionIdList;

}
