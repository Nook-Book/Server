package com.nookbook.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class NicknameCheckRes {

    @JsonProperty("is_unique")
    private boolean isUnique;

}
