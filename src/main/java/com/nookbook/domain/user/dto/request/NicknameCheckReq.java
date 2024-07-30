package com.nookbook.domain.user.dto.request;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Tag(name = "닉네임 중복 체크 요청", description = "사용자의 닉네임 입력을 받아 중복 여부를 판단합니다.")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NicknameCheckReq {

    private String nickname;

}
