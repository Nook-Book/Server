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
public class UserInfoRes {

    @Schema(type = "String", example = "minjufish", description = "사용자의 (이메일이 아닌) 친구 식별용 아이디입니다.")
    private String nicknameId;

    @Schema(type = "String", example = "기무라타쿠야가되", description = "사용자의 닉네임입니다.")
    private String nickname;

    @Schema(type = "String", example = "https://nookbook-s3-bucket.amazons.com/akfjvndij0e3.png", description = "사용자의 프로필 사진 URL입니다.")
    private String imageUrl;

    @Schema(type = "int", example = "12", description = "특정 사용자의 친구 수 입니다.")
    private int friendsNum;

    @Schema(type = "String", example = "REQUEST_SENT",
            description = "친구 신청 현황입니다. REQUEST_SENT: 내가 userId에게 친구 신청을 보냄, REQUEST_RECEIVED: userId가 나에게 친구 신청을 보냄, FRIEND_ACCEPT: 친구 상태, NONE: 요청을 보낸/받은 적 없음")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String requestStatus;
}
