package com.nookbook.global.payload;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Server (SVR)
    INTERNAL_SERVER_ERROR(500, "SVR001", "NookBook 서버 내부 오류입니다."),

    INVALID_PARAMETER(400, "E001", "잘못된 요청 데이터 입니다."),
    INVALID_REPRESENTATION(400, "E002", "잘못된 표현 입니다."),
    INVALID_FILE_PATH(400, "E003", "잘못된 파일 경로 입니다."),
    INVALID_OPTIONAL_ISPRESENT(400, "E004", "해당 값이 존재하지 않습니다."),
    INVALID_CHECK(400, "E005", "해당 값이 유효하지 않습니다."),
    INVALID_AUTHENTICATION(400, "E006", "잘못된 인증입니다."),
    INVALID_TOKEN(400, "E007", "잘못된 토큰입니다."),
    NOT_FOUND(404, "E008", "해당 데이터를 찾을 수 없습니다."),
    DUPLICATE_ERROR(409, "E009", "중복된 데이터가 존재합니다."),

    // User (USR)
    USER_DUPLICATE(409, "USR002", "이미 존재하는 사용자입니다."),
    USER_NOT_FOUND(404, "USR001", "해당 사용자를 찾을 수 없습니다."),

    // Challenge (CHL)
    CHALLENGE_NOT_AUTHORIZED(403, "CHL002", "챌린지에 대한 권한이 없습니다."),
    CHALLENGE_NOT_FOUND(404, "CHL001", "해당 챌린지를 찾을 수 없습니다."),
    CHALLENGE_OWNER_CANT_LEAVE(403, "CHL003", "챌린지 소유자는 나갈 수 없습니다."),
    PARTICIPANT_NOT_FOUND(404, "CHL004", "해당 챌린지 참여자를 찾을 수 없습니다."),
    PARTICIPANT_NOT_IN_CHALLENGE(403, "CHL003", "참가자가 챌린지에 속해있지 않습니다."),
    SELF_WAKE_UP_NOT_ALLOWED(403, "CHL005", "자기 자신에게는 깨우기 알림을 보낼 수 없습니다."),

    // Collection (COL)
    COLLECTION_NOT_AUTHORIZED(403, "COL005", "컬렉션에 대한 권한이 없습니다."),
    BOOK_NOT_IN_COLLECTION(400, "COL004", "컬렉션에 도서가 존재하지 않습니다."),
    BOOK_ALREADY_EXISTS_IN_COLLECTION(400, "COL005", "도서가 이미 존재합니다."),
    // collectionId
    COLLECTION_NOT_FOUND(404, "COL001", "해당 컬렉션을 찾을 수 없습니다."),


    // Book (BK)
    BOOK_NOT_FOUND(404, "BK001", "해당 도서를 찾을 수 없습니다."),

    // Alarm (ALM)
    ALARM_PUSH_SEND_FAILED(500, "ALM001", "푸시 알림 전송에 실패했습니다."),
    EXPO_TOKEN_NOT_FOUND(404, "ALM002", "해당 사용자의 Expo 토큰을 찾을 수 없습니다."),
    WAKE_UP_REQUEST_TOO_SOON(400, "ALM003", "깨우기 요청은 3시간에 한 번만 가능합니다."),;

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

}