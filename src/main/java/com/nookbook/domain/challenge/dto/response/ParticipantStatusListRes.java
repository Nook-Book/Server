package com.nookbook.domain.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ParticipantStatusListRes {
    @Schema(type = "boolean", example = "true", description = "해당 참여자가 사용자 본인인지에 대한 여부")
    private boolean isMe;

    @Schema(type = "Long", example = "1", description = "참여자 ID")
    private Long participantId;

    @Schema(type = "String", example = "밍쥬피쉬", description = "참여자 닉네임")
    private String nickname;

    @Schema(type = "String", example = "밍쥬피쉬증명사진.jpg", description = "참여자 프로필 이미지")
    private String participantImage;

    @Schema(type = "boolean", example = "true", description = "참여자 실시간 독서 진행 여부")
    private boolean isReading;

    @Schema(type = "String", example = "몰입", description = "참여자가 읽고 있는 도서 제목")
    private String readingBookTitle;

    @Schema(type = "String", example = "https://www.naver.com", description = "참여자가 읽고 있는 도서 이미지")
    private String readingBookImage;

    @Schema(type = "hh:mm:ss", example = "02:34:10", description = "참여자 일일 독서 시간 (타이머 기반)")
    private String dailyReadingTime;
}
