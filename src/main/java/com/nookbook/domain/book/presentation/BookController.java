package com.nookbook.domain.book.presentation;

import com.nookbook.domain.book.applicaiton.AladinService;
import com.nookbook.domain.book.dto.response.SearchRes;
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

}
