package com.nookbook.domain.note.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUrlRes {

    @Schema(type = "String", example = "https://nookbook-s3-bucket.amazon.com/dshkvsgaknfuv-djd.jpg", description = "이미지의 URL입니다.")
    private String imageUrl;
}
