package com.nookbook.domain.user.dto.request;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserInfoReq {
    private String nicknameId;
    private String nickname;
}
