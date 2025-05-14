package com.nookbook.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FriendRequestReq {

    @Schema(description = "친구 요청을 보낼 사용자의 userId", example = "1")
    private Long userId;
}
