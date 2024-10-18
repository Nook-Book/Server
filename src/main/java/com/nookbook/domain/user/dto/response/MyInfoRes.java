package com.nookbook.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyInfoRes {

    @Schema(type = "String", example = "minjufish", description = "사용자의 (이메일이 아닌) 친구 식별용 아이디입니다.")
    private String nicknameId;

    @Schema(type = "String", example = "기무라타쿠야가되", description = "사용자의 닉네임입니다.")
    private String nickname;

    @Schema(type = "int", example = "12", description = "특정 사용자의 친구 수 입니다.")
    private int friendsNum;
}
