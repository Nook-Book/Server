package com.nookbook.domain.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FriendListRes {
    @Schema(type = "Long", example = "1", description = "친구의 user id")
    private Long userId;

    @Schema(type = "String", example = "피쉬벅", description = "친구의 닉네임")
    private String nickname;

    @Schema(type = "String", example = "https://fishbuck.com", description = "친구의 프로필 이미지")
    private String profileImage;

}
