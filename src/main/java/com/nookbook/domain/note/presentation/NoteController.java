package com.nookbook.domain.note.presentation;

import com.nookbook.domain.note.application.NoteService;
import com.nookbook.domain.note.dto.request.CreateNoteReq;
import com.nookbook.domain.note.dto.request.UpdateNoteReq;
import com.nookbook.domain.note.dto.response.ImageUrlRes;
import com.nookbook.domain.note.dto.response.NoteDetailRes;
import com.nookbook.domain.note.dto.response.NoteRes;
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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Note", description = "Note API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class NoteController {

    private final NoteService noteService;

    @Operation(summary = "독서 노트 저장", description = "독서 노트를 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "저장 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @PostMapping("/books/{bookId}/notes")
    public ResponseEntity<?> createNote(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "도서의 id를 입력해주세요.", required = true) @PathVariable Long bookId,
            @Parameter(description = "Schemas의 CreateNoteReq를 참고해주세요.", required = true) @RequestBody CreateNoteReq createNoteReq
            ) {
        return noteService.saveNewNote(userPrincipal, bookId, createNoteReq);
    }

    @Operation(summary = "독서 노트 목록 조회", description = "독서 노트가 이미 존재할 경우, 노트의 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = NoteRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @GetMapping("/books/{bookId}/notes")
    public ResponseEntity<?> findNoteList(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "도서의 id를 입력해주세요.", required = true) @PathVariable Long bookId
    ) {
        return noteService.getUserPageNoteListByBookId(userPrincipal, userPrincipal.getId(), bookId);
    }

    @Operation(summary = "독서 노트 수정", description = "독서 노트의 제목과 내용, 공개 여부를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "수정 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @PatchMapping("/notes/{noteId}")
    public ResponseEntity<?> updateNote(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "노트의 id를 입력해주세요.", required = true) @PathVariable Long noteId,
            @Parameter(description = "Schemas의 UpdateNoteReq를 참고해주세요.", required = true) @RequestBody UpdateNoteReq updateNoteReq
            ) {
        return noteService.updateNote(userPrincipal, noteId, updateNoteReq);
    }

    @Operation(summary = "독서 노트 삭제", description = "독서 노트를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "삭제 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<?> deleteNote(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "노트의 id를 입력해주세요.", required = true) @PathVariable Long noteId
    ) {
        return noteService.deleteNote(userPrincipal, noteId);
    }

    @Operation(summary = "독서 노트 상세 조회", description = "독서 노트를 상세 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = NoteDetailRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @GetMapping("/notes/{noteId}")
    public ResponseEntity<?> findNoteDetail(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "노트의 id를 입력해주세요.", required = true) @PathVariable Long noteId
    ) {
        return noteService.getNoteDetail(userPrincipal, noteId);
    }

    @Operation(summary = "이미지 업로드", description = "이미지를 업로드하고, URL을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ImageUrlRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "업로드 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @PostMapping("/notes/images")
    public ResponseEntity<?> uploadNoteImage(
            @Parameter(description = "업로드할 이미지를 입력해주세요.", required = true) @RequestPart MultipartFile image
    ) {
        return noteService.uploadImage(image);
    }

    @Operation(summary = "이미지 삭제", description = "이미지를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "삭제 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    } )
    @DeleteMapping("/notes/images")
    public ResponseEntity<?> deleteNoteImage(
            @Parameter(description = "삭제할 이미지의 URL을 입력해주세요.", required = true) @RequestParam String imageUrl
    ) {
        return noteService.deleteImage(imageUrl);
    }

}
