package com.nookbook.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchUserRes {

    @Schema(type = "Long", example = "1", description = "사용자의 아이디입니다.")
    private Long userId;

    @Schema(type = "Long", example = "1", description = "친구 및 친구 요청의 아이디입니다.")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long friendId;

    @Schema(type = "String", example = "기무라타쿠야가되", description = "사용자의 닉네임입니다.")
    private String nickname;

    @Schema(type = "String", example = "https://nookbook-s3-bucket.amazons.com/akfjvndij0e3.png", description = "사용자의 프로필 사진 URL입니다.")
    private String imageUrl;
}
