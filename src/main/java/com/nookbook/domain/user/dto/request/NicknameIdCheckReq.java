package com.nookbook.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NicknameIdCheckReq {

    @Schema( type = "string", example = "string", description="사용자가 입력한 아이디입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{1,10}$")
    private String nicknameId;

}

