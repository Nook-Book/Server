package com.nookbook.domain.note.application;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.note.domain.Note;
import com.nookbook.domain.note.domain.repository.NoteRepository;
import com.nookbook.domain.note.dto.request.CreateNoteReq;
import com.nookbook.domain.note.dto.request.UpdateNoteReq;
import com.nookbook.domain.note.dto.response.NoteDetailRes;
import com.nookbook.domain.note.dto.response.NoteListRes;
import com.nookbook.domain.note.dto.response.NoteRes;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user_book.domain.BookStatus;
import com.nookbook.domain.user_book.domain.UserBook;
import com.nookbook.domain.user_book.domain.repository.UserBookRepository;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import com.nookbook.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserBookRepository userBookRepository;

    // 노트 저장
    @Transactional
    public ResponseEntity<?> saveNewNote(UserPrincipal userPrincipal, CreateNoteReq createNoteReq) {
        // User user = validUserById(userPrincipal.getId());
        User user = validUserById(1L);
        Book book = validBookById(createNoteReq.getBookId());

        UserBook userBook;
        // user_book에 없으면 생성
        Optional<UserBook> userBookOptional = userBookRepository.findByUserAndBook(user, book);
        if (userBookOptional.isEmpty()) {
            userBook = UserBook.builder()
                    .user(user)
                    .book(book)
                    .bookStatus(BookStatus.BEFORE_READ)  // 상태 변경은 수동으로
                    .build();
            userBookRepository.save(userBook);
        } else {
            userBook = userBookOptional.get();
            int noteLimit = noteRepository.countByUserBook(userBook);
            DefaultAssert.isTrue(noteLimit < 10, "한 책당 생성할 수 있는 노트의 최대 개수는 10개입니다.");
        }

        Note note = Note.builder()
                .title(createNoteReq.getTitle())
                .content(createNoteReq.getContent())
                .userBook(userBook)
                .build();
        noteRepository.save(note);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("기록이 저장되었습니다.")
                        .build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 노트 수정
    @Transactional
    public ResponseEntity<?> updateNote(UserPrincipal userPrincipal, Long noteId, UpdateNoteReq updateNoteReq) {
        // User user = validUserById(userPrincipal.getId());
        User user = validUserById(1L);
        Note note = validNoteById(noteId);
        DefaultAssert.isTrue(note.getUserBook().getUser() == user, "유효한 접근이 아닙니다.");

        note.updateNote(updateNoteReq.getTitle(), updateNoteReq.getContent());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder()
                        .message("기록이 수정되었습니다.")
                        .build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 노트 삭제
    @Transactional
    public ResponseEntity<?> deleteNote(UserPrincipal userPrincipal, Long noteId) {
        // User user = validUserById(userPrincipal.getId());
        User user = validUserById(1L);
        Note note = validNoteById(noteId);
        DefaultAssert.isTrue(note.getUserBook().getUser() == user, "유효한 접근이 아닙니다.");

        noteRepository.delete(note);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder()
                        .message("기록이 삭제되었습니다.")
                        .build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 책 정보 조회(제목, 이미지) && 노트 목록 조회
    public ResponseEntity<?> getNoteList(UserPrincipal userPrincipal, Long bookId) {
        // User user = validUserById(userPrincipal.getId());
        User user = validUserById(1L);
        Book book = validBookById(bookId);

        Optional<UserBook> userBookOptional = userBookRepository.findByUserAndBook(user, book);
        DefaultAssert.isTrue(userBookOptional.isPresent(), "기록이 존재하지 않습니다.");
        UserBook userBook = userBookOptional.get();

        int count = noteRepository.countByUserBook(userBook);

        List<Note> notes = noteRepository.findByUserBookOrderByCreatedAtDesc(userBook);
        List<NoteListRes> noteListRes = notes.stream()
                .map(note -> NoteListRes.builder()
                        .noteId(note.getNoteId())
                        .title(note.getTitle())
                        .createdDate(note.getCreatedAt().toLocalDate().toString())
                        .build())
                .toList();

        NoteRes noteRes = NoteRes.builder()
                .bookTitle(book.getTitle())
                .bookImage(book.getImage())
                .noteCount(count)
                .noteListRes(noteListRes)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(noteRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 노트 상세 조회
    public ResponseEntity<?> getNoteDetail(UserPrincipal userPrincipal, Long noteId) {
        // User user = validUserById(userPrincipal.getId());
        User user = validUserById(1L);
        Note note = validNoteById(noteId);
        DefaultAssert.isTrue(note.getUserBook().getUser() == user, "유효한 접근이 아닙니다.");

        NoteDetailRes noteDetailRes = NoteDetailRes.builder()
                .title(note.getTitle())
                .content(note.getContent())
                .createdDate(note.getCreatedAt().toLocalDate().toString())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(noteDetailRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private User validUserById(Long userId) {
        // Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> userOptional = userRepository.findById(1L);
        DefaultAssert.isTrue(userOptional.isPresent(), "유효한 사용자가 아닙니다.");
        return userOptional.get();
    }

    private Book validBookById(Long bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        DefaultAssert.isTrue(bookOptional.isPresent(), "해당 도서가 존재하지 않습니다.");
        return bookOptional.get();
    }

    private Note validNoteById(Long noteId) {
        Optional<Note> noteOptional = noteRepository.findById(noteId);
        DefaultAssert.isTrue(noteOptional.isPresent(), "해당 기록이 존재하지 않습니다.");
        return noteOptional.get();
    }
}
