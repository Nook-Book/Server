package com.nookbook.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FriendRequestDecisionReq {

    @Schema(type = "boolean", description = "요청 수락 여부로, true: 수락 / false: 거절", example = "true")
    private boolean accept;
}
