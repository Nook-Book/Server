package com.nookbook.domain.user.dto.request;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

@Tag(name = "사용자 닉네임/아이디 정보 저장", description = "사용자의 닉네임, 아이디 정보를 받아 저장합니다.")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserInfoReq {
    private String nicknameId;
    private String nickname;
}
