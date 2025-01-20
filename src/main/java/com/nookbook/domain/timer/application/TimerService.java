package com.nookbook.domain.timer.application;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.timer.domain.Timer;
import com.nookbook.domain.timer.domain.repository.TimerRepository;
import com.nookbook.domain.timer.dto.request.CreateTimerReq;
import com.nookbook.domain.timer.dto.response.TimerRecordRes;
import com.nookbook.domain.timer.dto.response.TimerRes;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user_book.domain.BookStatus;
import com.nookbook.domain.user_book.domain.UserBook;
import com.nookbook.domain.user_book.domain.repository.UserBookRepository;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimerService {

    private final UserRepository userRepository;
    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;
    private final TimerRepository timerRepository;

    // 타이머 저장
    @Transactional
    public ResponseEntity<?> saveTimerRecord(UserPrincipal userPrincipal, Long bookId, CreateTimerReq createTimerReq) {
        User user = validUserById(1L);
        Book book = validBookById(bookId);
        Optional<UserBook> userBookOptional = userBookRepository.findByUserAndBook(user, book);
        UserBook userBook = userBookOptional.get();
        plusTotalReadTime(userBook, userBook.getTotalReadTime(), createTimerReq.getTime());
        if (timerRepository.countByUserBook(userBook) >= 10) {
            Timer oldestTimer = timerRepository.findTop1ByUserBookOrderByCreatedAtAsc(userBook);
            timerRepository.delete(oldestTimer);
        }
        Timer timer = Timer.builder()
                .userBook(userBook)
                .readTime(createTimerReq.getTime())
                .build();
        timerRepository.save(timer);
        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("타이머가 저장되었습니다.")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private void plusTotalReadTime(UserBook userBook, BigInteger totalTime, BigInteger additionalTime) {
        BigInteger combinedTime = totalTime.add(additionalTime);
        userBook.updateTotalReadTime(combinedTime);
    }

    private String convertBigIntegerToString(BigInteger time) {
        long totalSeconds = time.longValue();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        // "HH:mm:ss" 형식으로 반환
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // 타이머 조회
    @Transactional
    public ResponseEntity<?> getTimerRecords(UserPrincipal userPrincipal, Long bookId) {
        User user = validUserById(1L);
        Book book = validBookById(bookId);
        Optional<UserBook> userBookOptional = userBookRepository.findByUserAndBook(user, book);
        UserBook userBook;
        if (userBookOptional.isEmpty()) {
            userBook = UserBook.builder()
                    .user(user)
                    .book(book)
                    .bookStatus(BookStatus.BEFORE_READ)
                    .build();
            userBookRepository.save(userBook);
        } else {
            userBook = userBookOptional.get();
        }
        List<Timer> timerList = timerRepository.findByUserBookOrderByCreatedAtDesc(userBook);
        List<TimerRecordRes> timerRecordRes = timerList.stream()
                .map(timer -> TimerRecordRes.builder()
                        .timerId(timer.getTimerId())
                        .date(timer.getCreatedAt().toLocalDate())
                        .time(timer.getCreatedAt().toLocalTime())
                        .readTime(convertBigIntegerToString(timer.getReadTime()))
                        .build())
                .toList();
        TimerRes timerRes = TimerRes.builder()
                .totalReadTime(convertBigIntegerToString(userBook.getTotalReadTime()))
                .recordResList(timerRecordRes)
                .build();
        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(timerRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private Book validBookById(Long bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        DefaultAssert.isTrue(bookOptional.isPresent(), "해당 책이 존재하지 않습니다.");
        return bookOptional.get();
    }

    private User validUserById(Long userId) {
        // Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> userOptional = userRepository.findById(1L);
        DefaultAssert.isTrue(userOptional.isPresent(), "유효한 사용자가 아닙니다.");
        return userOptional.get();
    }

}
