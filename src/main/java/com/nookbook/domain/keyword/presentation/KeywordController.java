package com.nookbook.domain.keyword.presentation;

import com.nookbook.domain.book.dto.response.KeywordRes;
import com.nookbook.domain.keyword.application.KeywordService;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ErrorResponse;
import com.nookbook.global.payload.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Keyword", description = "검색어 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/keywords")
public class KeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "검색어 조회", description = "사용자의 검색어를 최대 5개 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = KeywordRes.class))) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @GetMapping("")
    public ResponseEntity<?> findKeywords(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return keywordService.getKeywords(userPrincipal);
    }

    @Operation(summary = "검색어 삭제", description = "사용자의 검색어를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)) } ),
            @ApiResponse(responseCode = "400", description = "검색 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @DeleteMapping("/{keywordId}")
    public ResponseEntity<?> deleteKeyword(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "검색어의 id를 입력해주세요.", required = true) @PathVariable Long keywordId
    ) {
        return keywordService.deleteKeyword(userPrincipal, keywordId);
    }
}
