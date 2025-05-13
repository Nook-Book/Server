package com.nookbook.domain.user.presentation;

import com.nookbook.domain.book.applicaiton.BookService;
import com.nookbook.domain.book.dto.response.MostReadCategoriesRes;
import com.nookbook.domain.note.application.NoteService;
import com.nookbook.domain.note.dto.response.OtherUserNoteListRes;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.dto.request.NicknameCheckReq;
import com.nookbook.domain.user.dto.request.NicknameIdCheckReq;
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

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.Optional;

@Tag(name = "User MyPage", description = "User MyPage API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/my-page")
public class MyPageController {

    private final UserService userService;
    private final BookService bookService;
    private final NoteService noteService;

    @Operation(summary = "사용자 아이디 변경", description = "사용자의 아이디를 변경합니다.")
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

    @Operation(summary = "사용자 닉네임 변경", description = "사용자의 닉네임을 변경합니다.")
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

    @Operation(summary = "사용자 프로필 이미지 변경", description = "사용자의 프로필 이미지를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/image")
    public ResponseEntity<?> updateImage(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "변경할 프로필 이미지 파일") @RequestPart MultipartFile image
    ) {
        return userService.updateImage(userPrincipal, false, image);
    }

    @Operation(summary = "사용자 프로필 이미지를 기본으로 변경", description = "사용자의 프로필 이미지를 기본으로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "변경 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PutMapping("/image/default")
    public ResponseEntity<?> updateDefaultImage(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return userService.updateImage(userPrincipal, true, null);
    }


    @Operation(summary = "독서 통계", description = "마이페이지의 독서 통계(카테고리, 연도 및 월별)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MostReadCategoriesRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/report")
    public ResponseEntity<?> findMyReadingReport(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "독서 통계의 종류입니다. category: 카테고리별(기본) , year: 연도별", required = true) @RequestParam(defaultValue = "category") String type,
            @Parameter(description = "type이 year인 경우, 독서 통계를 확인하려는 연도를 입력해주세요") @RequestParam(required = false) Optional<Integer> targetYear
    ) {
        if (Objects.equals(type, "category")) {
            return bookService.countReadBooksByCategory(userPrincipal, userPrincipal.getId());
        } else if (Objects.equals(type, "year") && targetYear.isPresent()) {
            return bookService.countReadBooksByYear(userPrincipal, userPrincipal.getId(), targetYear);
        } else throw new InvalidParameterException("유효한 파라미터가 아닙니다.");
    }

    @Operation(summary = "내 정보 조회", description = "마이페이지의 정보(아이디, 닉네임, 프로필 이미지, 친구 수)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("")
    public ResponseEntity<?> findMyInformation(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return userService.getUserInfo(userPrincipal, userPrincipal.getId());
    }

    @Operation(summary = "기록 전체보기 목록 조회 및 검색", description = "마이페이지의 기록 전체보기 목록 조회 또는 검색하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = OtherUserNoteListRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/note")
    public ResponseEntity<?> findMyAllNotes(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "검색하고자 하는 단어를 입력해주세요. 없다면 입력하지 않습니다.") @RequestParam(required = false) String keyword
    ) {
        return noteService.getUserPageNoteList(userPrincipal, userPrincipal.getId(), keyword);
    }

    @Operation(summary = "기록 전체보기 상세 조회", description = "마이페이지의 기록 전체보기에서 도서를 선택하여 상세 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = OtherUserNoteListRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/note/book/{bookId}")
    public ResponseEntity<?> findMyAllNotesDetail(
            @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "조회하고자 하는 노트의 **도서 id**를 입력해주세요.", required = true) @PathVariable Long bookId
    ) {
        return noteService.getUserPageNoteListByBookId(userPrincipal, userPrincipal.getId(), bookId);
    }

}
