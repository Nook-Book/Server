package com.nookbook.domain.book.presentation;

import com.nookbook.domain.book.applicaiton.AladinService;
import com.nookbook.domain.book.dto.response.BestSellerRes;
import com.nookbook.domain.book.dto.response.KeywordRes;
import com.nookbook.domain.book.dto.response.SearchRes;
import com.nookbook.domain.keyword.application.KeywordService;
import com.nookbook.domain.user.dto.request.UserInfoReq;
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
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Book", description = "Book API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/book")
public class BookController {

    private final AladinService aladinService;
    private final KeywordService keywordService;

    @Operation(summary = "도서 검색", description = "도서를 제목, 저자, 출판사로 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SearchRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "검색 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @GetMapping("/search")
    public ResponseEntity<?> findBooksByKeyword(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "검색어를 입력해주세요.", required = true) @RequestParam String keyword,
            @Parameter(description = "검색된 도서 목록을 페이지별로 조회합니다. **Page는 1부터 시작합니다!**", required = true) @RequestParam(defaultValue = "1") int page
            ) {
        return aladinService.searchBooks(userPrincipal, keyword, page);
    }

    @Operation(summary = "베스트셀러 조회", description = "베스트셀러를 카테고리별로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BestSellerRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "검색 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @GetMapping("/best-seller")
    public ResponseEntity<?> findBestSellers(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "베스트셀러의 카테고리를 입력해주세요. 종합(0), 소설(1), 경제/경영(170), 자기계발(336), 시(50940), 에세이(55889), 인문/교양(656), 취미/실용(55890), 매거진(2913), 기본값은 종합(0)입니다.", required = true) @RequestParam(defaultValue = "0") int category,
            @Parameter(description = "page의 size입니다. 기본 값은 size입니다.", required = true) @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "베스트셀러를 페이지별로 조회합니다. **Page는 1부터 시작합니다!**", required = true) @RequestParam(defaultValue = "1") int page
    ) {
        return aladinService.getBestSellerByCategory(page, category, size);
    }

    @Operation(summary = "검색어 조회", description = "사용자의 검색어를 최대 5개 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = KeywordRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @GetMapping("/keyword")
    public ResponseEntity<?> findKeywords(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return keywordService.getKeywords(userPrincipal);
    }

    @Operation(summary = "검색어 삭제", description = "사용자의 검색어를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SearchRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "검색 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @DeleteMapping("/keyword/{keywordId}")
    public ResponseEntity<?> deleteKeyword(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "검색어의 id를 입력해주세요.", required = true) @PathVariable Long keywordId
    ) {
        return keywordService.deleteKeyword(userPrincipal, keywordId);
    }

}
