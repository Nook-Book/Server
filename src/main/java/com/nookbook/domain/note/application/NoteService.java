package com.nookbook.domain.note.application;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.note.domain.Note;
import com.nookbook.domain.note.domain.repository.NoteRepository;
import com.nookbook.domain.note.dto.request.CreateNoteReq;
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
    public ResponseEntity<?> saveNewNote(UserPrincipal userPrincipal, Long bookId, CreateNoteReq createNoteReq) {
        User user = validUserById(userPrincipal.getId());
        Book book = validBookById(bookId);

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
        } else userBook = userBookOptional.get();

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
    // 노트 삭제

    // 책 정보 조회(제목, 이미지) && 노트 목록 조회
    // 처음 아닐 경우에만 보여지므로, book 엔티티에서 조회

    private User validUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isTrue(userOptional.isPresent(), "유효한 사용자가 아닙니다.");
        return userOptional.get();
    }

    private Book validBookById(Long bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        DefaultAssert.isTrue(bookOptional.isPresent(), "해당 도서가 존재하지 않습니다.");
        return bookOptional.get();
    }
}
