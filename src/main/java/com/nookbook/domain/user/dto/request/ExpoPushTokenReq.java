package com.nookbook.domain.user.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExpoPushTokenReq {

    @Schema(example = "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxx]")
    private String expoPushToken;

}