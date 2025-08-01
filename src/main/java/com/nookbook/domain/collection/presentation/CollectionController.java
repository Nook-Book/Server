package com.nookbook.domain.collection.presentation;


import com.nookbook.domain.collection.application.CollectionService;
import com.nookbook.domain.collection.dto.request.*;
import com.nookbook.domain.collection.dto.response.CollectionBooksListRes;
import com.nookbook.domain.collection.dto.response.CollectionListRes;
import com.nookbook.domain.collection.dto.response.MainCollectionListRes;
import com.nookbook.global.config.security.token.CurrentUser;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.CommonApiResponse;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/collections")
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

    // 사용자의 컬렉션 목록 조회 API
    @Operation(summary = "사용자의 컬렉션 목록 조회 API", description = "유저의 컬렉션 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CollectionListRes.class))}),
            @ApiResponse(responseCode = "400", description = "컬렉션 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("")
    public ResponseEntity<?> getCollectionList(
            @Parameter @CurrentUser UserPrincipal userPrincipal
    ) {
        CollectionListRes result = collectionService.getCollectionList(userPrincipal);
        return ResponseEntity.ok(CommonApiResponse.success(result));
    }

    // 다른 사용자의 컬렉션 목록 조회 API
    @Operation(summary = "다른 사용자(친구)의 컬렉션 목록 조회 API", description = "친구의 컬렉션 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구의 컬렉션 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CollectionListRes.class))}),
            @ApiResponse(responseCode = "400", description = "친구의 컬렉션 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getFriendCollectionList(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long userId
    ) {
        CollectionListRes result = collectionService.getFriendCollectionList(userPrincipal, userId);
        return ResponseEntity.ok(CommonApiResponse.success(result));
    }


    @Operation(summary = "컬렉션 제목 수정 API", description = "유저의 컬렉션 제목을 수정하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 제목 수정 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "컬렉션 제목 수정 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/{collectionId}/title")
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

    // 친구 컬렉션 등록 도서 목록 조회 API
    @Operation(summary = "친구 컬렉션 등록 도서 목록 조회 API", description = "친구의 컬렉션에 등록된 도서 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구의 컬렉션 등록 도서 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CollectionBooksListRes.class))}),
            @ApiResponse(responseCode = "400", description = "친구의 컬렉션 등록 도서 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{userId}/{collectionId}/books")
    public ResponseEntity<?> getFriendCollectionBooks(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long userId,
            @PathVariable Long collectionId
    ) {
        CollectionBooksListRes result = collectionService.getFriendCollectionBooks(userPrincipal, userId, collectionId);
        return ResponseEntity.ok(CommonApiResponse.success(result));
    }


    @Operation(summary = "컬렉션 도서 삭제 API", description = "컬렉션에 등록된 도서를 삭제하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 도서 삭제 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "컬렉션 도서 삭제 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/{collectionId}/books/delete")
    public ResponseEntity<?> deleteBookFromCollection(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long collectionId,
            @RequestBody DeleteBookReq deleteBookReq
    ) {
        return collectionService.deleteBookFromCollection(userPrincipal, collectionId, deleteBookReq);
    }


    @Operation(summary = "컬렉션 순서 편집 API", description = "컬렉션의 순서를 변경하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 등록 도서 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "컬렉션 등록 도서 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/order")
    public ResponseEntity<?> editCollectionOrder(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @RequestBody List<CollectionOrderReq> collectionOrderReqList
    ) {
        return collectionService.editCollectionOrder(userPrincipal, collectionOrderReqList);
    }


    @Operation(summary = "컬렉션 도서 추가 API", description = "컬렉션에 도서를 추가하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 도서 추가 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "컬렉션 도서 추가 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("{collectionId}/books/{bookId}")
    public ResponseEntity<?> addBookToCollection(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long collectionId,
            @PathVariable Long bookId
    ) {
        return collectionService.addBookToCollection(userPrincipal, collectionId, bookId);
    }

    @Operation(summary = "현재 컬렉션 상세 조회 API", description = "현재 컬렉션에 등록된 도서 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "현재 컬렉션 상세 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MainCollectionListRes.class))}),
            @ApiResponse(responseCode = "400", description = "현재 컬렉션 상세 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentCollectionBooks(
            @Parameter @CurrentUser UserPrincipal userPrincipal
    ) {
        return collectionService.getCurrentCollectionBooks(userPrincipal);
    }

    @Operation(summary = "컬렉션 삭제 API", description = "특정 컬렉션을 삭제하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 삭제 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "컬렉션 삭제 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping("/{collectionId}")
    public ResponseEntity<?> deleteCollection(
            @Parameter @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long collectionId
    ) {
        return collectionService.deleteCollection(userPrincipal, collectionId);
    }

    @Operation(summary = "컬렉션 내 도서 이동 API", description = "컬렉션 내 도서를 다른 컬렉션으로 이동하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "컬렉션 내 도서 이동 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "컬렉션 내 도서 이동 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{collectionId}/books/{targetCollectionId}")
    public ResponseEntity<CommonApiResponse<String>> moveBookToAnotherCollection(
            @Parameter(description = "로그인한 사용자") @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long collectionId,
            @PathVariable Long targetCollectionId,
            @RequestBody BookIdListReq bookIdListReq
    ) {
        collectionService.moveBookToAnotherCollection(userPrincipal, collectionId, targetCollectionId, bookIdListReq);
        return ResponseEntity.ok(CommonApiResponse.success("도서가 컬렉션 간에 성공적으로 이동되었습니다."));
    }

}
