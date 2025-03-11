package com.nookbook.domain.user.presentation;

import com.nookbook.domain.book.applicaiton.BookService;
import com.nookbook.domain.book.dto.response.BookStatisticsRes;
import com.nookbook.domain.book.dto.response.MostReadCategoriesRes;
import com.nookbook.domain.note.application.NoteService;
import com.nookbook.domain.note.dto.response.OtherUserNoteListRes;
import com.nookbook.domain.user.application.FriendService;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.dto.request.NicknameCheckReq;
import com.nookbook.domain.user.dto.request.NicknameIdCheckReq;
import com.nookbook.domain.user.dto.response.FriendsRequestRes;
import com.nookbook.domain.user.dto.response.SearchUserRes;
import com.nookbook.domain.user.dto.response.UserInfoRes;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Tag(name = "User MyPage", description = "User MyPage API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/my-page")
public class MyPageController {

    private final UserService userService;
    private final BookService bookService;
    private final NoteService noteService;
    private final FriendService friendService;

    @Operation(summary = "[마이페이지] 사용자 아이디 변경", description = "사용자의 아이디를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/nickname-id")
    public ResponseEntity<?> updateNicknameId(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "변경할 아이디 입력값", required = true) @Valid @RequestBody NicknameIdCheckReq nicknameIdCheckReq
    ) {
        return userService.updateNicknameId(userPrincipal, nicknameIdCheckReq);
    }

    @Operation(summary = "[마이페이지] 사용자 닉네임 변경", description = "사용자의 닉네임을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/nickname")
    public ResponseEntity<?> updateNickname(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "변경할 닉네임 입력값", required = true) @Valid @RequestBody NicknameCheckReq nicknameCheckReq
    ) {
        return userService.updateNickname(userPrincipal, nicknameCheckReq);
    }

    @Operation(summary = "[마이페이지] 사용자 프로필 이미지 변경", description = "사용자의 프로필 이미지를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/image")
    public ResponseEntity<?> updateImage(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "기본 이미지 사용 여부", required = true) @RequestPart Boolean isDefaultImage,
            @Parameter(description = "변경할 프로필 이미지 파일") @RequestPart Optional<MultipartFile> image
    ) {
        return userService.updateImage(userPrincipal, isDefaultImage, image);
    }

    @Operation(summary = "[마이페이지] 독서 통계(카테고리별)", description = "마이페이지 또는 친구페이지의 독서 통계(카테고리)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MostReadCategoriesRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/report/category")
    public ResponseEntity<?> findReadingReportByCategory(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "독서 통계(카테고리별)을 조회하고자 하는 사용자의 id를 입력해주세요. 내 통계인 경우 userId는 null로 전달합니다.") @RequestParam(required = false) Long userId
    ) {
        return bookService.countReadBooksByCategory(userPrincipal, userId);
    }

    @Operation(summary = "[마이페이지] 독서 통계(연도 및 월별)", description = "마이페이지 또는 친구페이지의 독서 통계(연도 및 월별)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BookStatisticsRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/report")
    public ResponseEntity<?> findReadingReportByYear(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "독서 통계(연도 및 월별)을 조회하고자 하는 사용자의 id를 입력해주세요. 내 통계인 경우 userId는 null로 전달합니다.") @RequestParam(required = false) Long userId,
            @Parameter(description = "독서 통계를 확인하려는 연도를 입력해주세요", required = true) @RequestParam int year
    ) {
        return bookService.countReadBooksByYear(userPrincipal, userId, year);
    }

    @Operation(summary = "[마이페이지] 정보 조회", description = "마이페이지 또는 친구페이지의 정보(아이디, 닉네임, 프로필 이미지, 친구 수)를 조회합니다. 친구의 페이지인 경우 친구 상태를 추가하여 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("")
    public ResponseEntity<?> findUserInformation(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "조회하고자 하는 사용자의 id를 입력해주세요. 나의 프로필인 경우 userId는 null로 전달합니다.") @RequestParam(required = false) Long userId
    ) {
        return userService.getUserInfo(userPrincipal, userId);
    }

    @Operation(summary = "[마이페이지] 내 기록 전체보기 목록 조회 및 검색", description = "마이페이지(사용자 본인)의 기록 전체보기 목록 조회 또는 검색하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = OtherUserNoteListRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/note")
    public ResponseEntity<?> findUserAllNotes(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "검색하고자 하는 단어를 입력해주세요. 없다면 입력하지 않습니다.") @RequestParam(required = false) String keyword
    ) {
        return noteService.getMyNoteList(userPrincipal, keyword);
    }

    @Operation(summary = "[마이페이지] 친구 기록 전체보기 목록 조회 및 검색", description = "마이페이지(친구)의 기록 전체보기 목록 조회 또는 검색하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = OtherUserNoteListRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/{userId}/note")
    public ResponseEntity<?> findFriendAllNotes(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "조회하고자 하는 사용자의 id를 입력해주세요.") @PathVariable Long userId,
            @Parameter(description = "검색하고자 하는 단어를 입력해주세요. 없다면 입력하지 않습니다.") @RequestParam(required = false) String keyword
    ) {
        return noteService.getFriendNoteList(userPrincipal, userId, keyword);
    }

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
            @Parameter(description = "조회하고자 하는 사용자의 id를 입력해주세요.") @PathVariable Long userId
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
            @Parameter(description = "친구 요청 목록에서 조회한 friendId를 입력해주세요.") @PathVariable Long friendId,
            @Parameter(description = "요청의 수락 여부입니다. true: 수락, false: 거절") @RequestParam boolean isAccept
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
            @Parameter(description = "친구 요청 목록에서 조회한 friendId를 입력해주세요.") @PathVariable Long friendId
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
            @Parameter(description = "친구 목록에서 조회한 friendId를 입력해주세요.") @PathVariable Long friendId
    ) {
        return friendService.deleteFriendRequest(userPrincipal, friendId, true);
    }

}
