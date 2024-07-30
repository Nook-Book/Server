package com.nookbook.domain.user.dto.request;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserInfoReq {
    private String nicknameId;
    private String nickname;
}
