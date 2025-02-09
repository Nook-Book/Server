package com.nookbook.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendsRequestRes {

    @Schema(description = "Schemas의 SearchUserRes를 확인해주세요. 내가 보낸 요청의 목록입니다.")
    private List<SearchUserRes> sentRequest;

    @Schema(description = "Schemas의 SearchUserRes를 확인해주세요. 내가 받은 요청의 목록입니다.")
    private List<SearchUserRes> receivedRequest;
}
