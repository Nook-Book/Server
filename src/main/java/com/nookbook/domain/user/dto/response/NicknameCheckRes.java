package com.nookbook.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

@Tag(name = "닉네임 중복 체크 응답", description = "사용자의 닉네임이 중복인지 여부를 반환합니다.")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class NicknameCheckRes {

    @JsonProperty("is_unique")
    private boolean isUnique;

}
