package com.nookbook.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NicknameCheckReq {

    @Schema( type = "string", example = "string", description="사용자가 입력한 닉네임입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,12}$")
    private String nickname;

}
