package com.nookbook.domain.auth.dto.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TokenMapping {
    private String email;
    private String accessToken;
    private String refreshToken;
}
