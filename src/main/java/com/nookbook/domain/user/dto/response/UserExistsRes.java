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
public class UserExistsRes {
    @Schema(type = "boolean", example = "true", description = "사용자가 기존에 가입된 경우 true, 신규 사용자면 false를 반환합니다.")
    private boolean isRegistered;
}
