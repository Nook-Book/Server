package com.nookbook.domain.collection.presentation;


import com.nookbook.domain.collection.application.CollectionService;
import com.nookbook.domain.collection.dto.request.CollectionCreateReq;
import com.nookbook.domain.collection.dto.request.UpdateCollectionTitleReq;
import com.nookbook.domain.collection.dto.response.CollectionBooksListRes;
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

    @Operation(summary = "컬렉션 제목 수정 API", description = "유저의 컬렉션 제목을 수정하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 제목 수정 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "컬렉션 제목 수정 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/{collectionId}")
    public ResponseEntity<?> updateCollectionTitle(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long collectionId,
            @RequestBody UpdateCollectionTitleReq updateCollectionTitleReq
    ) {
        return collectionService.updateCollectionTitle(userPrincipal, collectionId, updateCollectionTitleReq);
    }

    @Operation(summary = "컬렉션 등록 도서 목록 조회 API", description = "컬렉션에 등록된 도서 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 등록 도서 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CollectionBooksListRes.class))}),
            @ApiResponse(responseCode = "400", description = "컬렉션 등록 도서 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{collectionId}/books")
    public ResponseEntity<?> getCollectionBooks(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long collectionId
    ) {
        return collectionService.getCollectionBooks(userPrincipal, collectionId);
    }

//    @Operation(summary = "컬렉션 순서 변경 API", description = "컬렉션의 순서를 변경하는 API입니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "컬렉션 등록 도서 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CollectionBooksListRes.class))}),
//            @ApiResponse(responseCode = "400", description = "컬렉션 등록 도서 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
//    })


    @Operation(summary = "컬렉션 도서 추가 API", description = "컬렉션에 도서를 추가하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 도서 추가 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "컬렉션 도서 추가 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("{collectionId}/{bookId}")
    public ResponseEntity<?> addBookToCollection(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long collectionId,
            @PathVariable Long bookId
    ) {
        return collectionService.addBookToCollection(userPrincipal, collectionId, bookId);
    }





}
