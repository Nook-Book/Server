package com.nookbook.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class NicknameIdCheckRes {

    @JsonProperty("is_unique")
    private boolean isUnique;

}
