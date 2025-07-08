package com.nookbook.domain.user.presentation;

import com.nookbook.domain.book.application.BookService;
import com.nookbook.domain.book.dto.response.MostReadCategoriesRes;
import com.nookbook.domain.note.application.NoteService;
import com.nookbook.domain.note.dto.response.OtherUserNoteListRes;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.dto.request.ExpoPushTokenReq;
import com.nookbook.domain.user.dto.request.NicknameIdCheckReq;
import com.nookbook.domain.user.dto.request.NicknameCheckReq;
import com.nookbook.domain.user.dto.request.UserInfoReq;
import com.nookbook.domain.user.dto.response.*;
import com.nookbook.domain.user_book.application.UserBookService;
import com.nookbook.domain.user_book.dto.response.DailyUserBookCalendarRes;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ErrorResponse;
import com.nookbook.global.payload.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.Optional;

@Tag(name = "User", description = "User API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final NoteService noteService;
    private final BookService bookService;
    private final UserBookService userBookService;

    @Operation(summary = "기존 사용자 여부 확인", description = "기존에 가입된 사용자인지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 존재 여부 확인 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserExistsRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "사용자 존재 여부 확인 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/exists")
    public ResponseEntity<?> checkUserExists(@CurrentUser UserPrincipal userPrincipal) {
        return userService.checkUserExists(userPrincipal);
    }

    // 회원가입 과정 (초기에만 이루어지므로, 기본 컬렉션 로직 포함)
    @Operation(summary = "사용자 정보 등록", description = "사용자가 설정한 정보를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 등록 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "사용자 정보 등록 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/info")
    public ResponseEntity<?> saveUserInfo(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "유저 닉네임/아이디", required = true)
            @RequestBody UserInfoReq userInfoReq
    ) {
        return userService.saveUserInfo(userPrincipal, userInfoReq);
    }

    @Operation(summary = "사용자 아이디 중복 확인", description = "사용자가 입력한 아이디가 중복인지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능한 아이디", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = NicknameIdCheckRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "사용 불가한 아이디", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/nickname-id")
    public ResponseEntity<?> checkNicknameId(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "중복 여부를 확인할 아이디 입력값", required = true)
            @Valid @RequestBody NicknameIdCheckReq nicknameIdCheckReq
    ) {
        return userService.checkNicknameId(userPrincipal, nicknameIdCheckReq);
    }


    @Operation(summary = "사용자 닉네임 중복 확인", description = "사용자가 입력한 닉네임이 중복인지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = NicknameCheckRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "사용 불가한 닉네임", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/nickname")
    public ResponseEntity<?> checkNickname(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "중복 여부를 확인할 닉네임 입력값", required = true)
            @Valid @RequestBody NicknameCheckReq nicknameCheckReq
    ) {
        return userService.checkNickname(userPrincipal, nicknameCheckReq);
    }

    @Operation(summary = "Expo push token 저장", description = "사용자의 Expo push token을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expo push token 저장 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "Expo push token 저장 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/expo-push-token")
    public ResponseEntity<?> saveExpoPushToken(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody ExpoPushTokenReq expoPushTokenReq
    ) {
        return userService.saveExpoPushToken(userPrincipal, expoPushTokenReq);
    }


    // 친구페이지
    @Operation(summary = "사용자 검색", description = "친구 추가 페이지에서 사용자 전체를 대상으로 단어를 검색하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SearchUserRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchAllUsers(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "검색하고자 하는 단어를 입력해주세요.") @RequestParam String keyword,
            @Parameter(description = "페이지의 숫자입니다. 페이지는 0부터 시작합니다.") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 내 요소의 개수입니다.") @RequestParam(defaultValue = "20") int size
    ) {
        return userService.searchUsers(userPrincipal, keyword, page, size);
    }

    @Operation(summary = "독서 통계 조회", description = "친구페이지의 독서 통계(카테고리, 연도 및 월별 선택)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MostReadCategoriesRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/{userId}/reports")
    public ResponseEntity<?> findReadingReport(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "독서 통계를 조회하고자 하는 사용자의 id를 입력해주세요.", required = true) @PathVariable Long userId,
            @Parameter(description = "독서 통계의 종류입니다. category: 카테고리별(기본) , year: 연도별", required = true) @RequestParam(defaultValue = "category") String type,
            @Parameter(description = "type이 year인 경우, 독서 통계를 확인하려는 연도를 입력해주세요") @RequestParam(required = false) Optional<Integer> targetYear
    ) {
        if (Objects.equals(type, "category")) {
            return bookService.countReadBooksByCategory(userPrincipal, userId);
        } else if (Objects.equals(type, "year") && targetYear.isPresent()) {
            return bookService.countReadBooksByYear(userPrincipal, userId, targetYear);
        } else throw new InvalidParameterException("유효한 파라미터가 아닙니다.");
    }

    @Operation(summary = "사용자 정보 조회", description = "친구페이지의 정보(아이디, 닉네임, 프로필 이미지, 친구 수, 친구 요청 상태)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> findUserInformation(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "조회하고자 하는 사용자의 id를 입력해주세요.", required = true) @PathVariable Long userId
    ) {
        return userService.getUserInfo(userPrincipal, userId);
    }

    @Operation(summary = "기록 전체보기 목록 조회 및 검색", description = "친구페이지의 기록 전체보기 목록 조회 또는 검색하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = OtherUserNoteListRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/{userId}/books")
    public ResponseEntity<?> findUserAllNotes(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "조회하고자 하는 사용자의 id를 입력해주세요.", required = true) @PathVariable Long userId,
            @Parameter(description = "검색하고자 하는 단어를 입력해주세요. 없다면 입력하지 않습니다.") @RequestParam(required = false) String keyword
    ) {
        return noteService.getUserPageNoteList(userPrincipal, userId, keyword);
    }

    @Operation(summary = "기록 전체보기 상세 조회", description = "친구페이지의 기록 전체보기에서 도서를 선택하여 상세 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = OtherUserNoteListRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/{userId}/books/{bookId}/notes")
    public ResponseEntity<?> findUserAllNotesDetail(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "조회하고자 하는 사용자의 id를 입력해주세요.", required = true) @PathVariable Long userId,
            @Parameter(description = "조회하고자 하는 노트의 **도서 id**를 입력해주세요.", required = true) @PathVariable Long bookId
    ) {
        return noteService.getUserPageNoteListByBookId(userPrincipal, userId, bookId);
    }


    // 특정 유저의 독서 기록 정보 조회 API
    // 날짜 형식: 2021-11-01 또는 2021-11
    @Operation(summary = "특정 유저의 날짜별 독서 기록 조회", description = "특정 유저의 날짜별 독서 기록을 조회합니다.")
    @GetMapping("/{userId}/calendar/{date}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "독서 캘린더 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = DailyUserBookCalendarRes.class)) } ),
            @ApiResponse(responseCode = "400", description = "독서 캘린더 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    public ResponseEntity<?> getUserBookCalendar(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "사용자 ID를 입력해주세요.", required = true) @PathVariable Long userId,
            @Parameter(description = "조회할 날짜를 입력해주세요.", example = "2021-11-01", required = true) @PathVariable String date
    ) {
        return userBookService.getUserBookCalendar(userPrincipal, userId, date);
    }
}
