package com.nookbook.domain.collection.presentation;


import com.nookbook.domain.collection.application.CollectionService;
import com.nookbook.domain.collection.dto.request.CollectionCreateReq;
import com.nookbook.domain.collection.dto.response.CollectionListRes;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/collection")
@Tag(name = "Collection", description = "도서 컬렉션 관련 API입니다.")
public class CollectionController {

    private final CollectionService collectionService;

    @Operation(summary = "새 컬렉션 생성 API", description = "새 컬렉션을 생성하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 생성 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "컬렉션 생성 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/new")
    public ResponseEntity<?> createCollection(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @RequestBody CollectionCreateReq collectionCreateReq
    ) {
        return collectionService.createCollection(userPrincipal, collectionCreateReq);
    }

    @Operation(summary = "컬렉션 목록 조회 API", description = "유저의 컬렉션 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CollectionListRes.class))}),
            @ApiResponse(responseCode = "400", description = "컬렉션 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/list")
    public ResponseEntity<?> getCollectionList(
            @Parameter @CurrentUser UserPrincipal userPrincipal
    ) {
        return collectionService.getCollectionList(userPrincipal);
    }


}
