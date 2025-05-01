package com.nookbook.domain.user.presentation;

import com.nookbook.domain.book.applicaiton.BookService;
import com.nookbook.domain.book.dto.response.MostReadCategoriesRes;
import com.nookbook.domain.note.application.NoteService;
import com.nookbook.domain.note.dto.response.OtherUserNoteListRes;
import com.nookbook.domain.user.application.FriendService;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.dto.response.FriendsRequestRes;
import com.nookbook.domain.user.dto.response.SearchUserRes;
import com.nookbook.domain.user.dto.response.UserInfoRes;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.Optional;

@Tag(name = "Friend", description = "Friend API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FriendController {

    private final UserService userService;
    private final BookService bookService;
    private final FriendService friendService;
    private final NoteService noteService;

    @Operation(summary = "친구 추가 - 검색", description = "사용자 전체를 대상으로 단어를 검색하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SearchUserRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/friend/all")
    public ResponseEntity<?> searchAllUsers(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "검색하고자 하는 단어를 입력해주세요.") @RequestParam String keyword,
            @Parameter(description = "페이지의 숫자입니다. 페이지는 0부터 시작합니다.") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 내 요소의 개수입니다.") @RequestParam(defaultValue = "20") int size
    ) {
        return friendService.searchUsers(userPrincipal, keyword, page, size);
    }

    @Operation(summary = "친구 목록 - 조회", description = "친구 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SearchUserRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/friend")
    public ResponseEntity<?> getFriends(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return friendService.getFriends(userPrincipal, null);
    }

    @Operation(summary = "친구 추가 - 내가 보낸/받은 요청 목록 조회", description = "내가 보낸/받은 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = FriendsRequestRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/friend/pending")
    public ResponseEntity<?> getFriendsRequest(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return friendService.getFriendRequestList(userPrincipal);
    }

    @Operation(summary = "친구 요청", description = "다른 사용자에게 친구를 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class) ) } ),
            @ApiResponse(responseCode = "400", description = "저장 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/friend/pending/{userId}")
    public ResponseEntity<?> sendFriendRequest(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "조회하고자 하는 사용자의 id를 입력해주세요.", required = true) @PathVariable Long userId
    ) {
        return friendService.sendFriendRequest(userPrincipal, userId);
    }

    @Operation(summary = "친구 수락/거절", description = "요청을 수락하거나 거절합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class) ) } ),
            @ApiResponse(responseCode = "400", description = "저장 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/friend/pending/{friendId}")
    public ResponseEntity<?> acceptOrRejectFriendRequest(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "친구 요청 목록에서 조회한 friendId를 입력해주세요.", required = true) @PathVariable Long friendId,
            @Parameter(description = "요청의 수락 여부입니다. true: 수락, false: 거절", required = true) @RequestParam boolean isAccept
    ) {
        return friendService.updateFriendRequestStatus(userPrincipal, friendId, isAccept);
    }

    @Operation(summary = "친구 요청 취소", description = "내가 보낸 친구 요청을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "취소 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class) ) } ),
            @ApiResponse(responseCode = "400", description = "취소 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @DeleteMapping ("/friend/pending/{friendId}")
    public ResponseEntity<?> cancelFriendRequest(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "친구 요청 목록에서 조회한 friendId를 입력해주세요.", required = true) @PathVariable Long friendId
    ) {
        return friendService.deleteFriendRequest(userPrincipal, friendId, false);
    }

    @Operation(summary = "친구 삭제", description = "친구를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class) ) } ),
            @ApiResponse(responseCode = "400", description = "삭제 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @DeleteMapping ("/friend/{friendId}")
    public ResponseEntity<?> deleteFriendRequest(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "친구 목록에서 조회한 friendId를 입력해주세요.", required = true) @PathVariable Long friendId
    ) {
        return friendService.deleteFriendRequest(userPrincipal, friendId, true);
    }

    // 친구페이지
    @Operation(summary = "독서 통계 조회", description = "친구페이지의 독서 통계(카테고리, 연도 및 월별 선택)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MostReadCategoriesRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/user/{userId}/report")
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
    @GetMapping("/user/{userId}")
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
    @GetMapping("/user/{userId}/note")
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
    @GetMapping("/user/{userId}/note/book/{bookId}")
    public ResponseEntity<?> findUserAllNotesDetail(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "조회하고자 하는 사용자의 id를 입력해주세요.", required = true) @PathVariable Long userId,
            @Parameter(description = "조회하고자 하는 노트의 **도서 id**를 입력해주세요.", required = true) @PathVariable Long bookId
    ) {
        return noteService.getUserPageNoteListByBookId(userPrincipal, userId, bookId);
    }
}
