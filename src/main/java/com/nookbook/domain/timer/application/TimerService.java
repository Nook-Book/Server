package com.nookbook.domain.timer.application;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.timer.domain.Timer;
import com.nookbook.domain.timer.domain.repository.TimerRepository;
import com.nookbook.domain.timer.dto.request.UpdateTimerReq;
import com.nookbook.domain.timer.dto.response.StartTimerIdRes;
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
import java.time.LocalDate;
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

    // 타이머 시작
    @Transactional
    public ResponseEntity<?> createTimer(UserPrincipal userPrincipal, Long bookId) {
        User user = validUserById(userPrincipal.getId());
        Book book = validBookById(bookId);
        //if (timerRepository.countByUserBook(userBook) >= 10) {
        //    deleteOldestTimer(userBook);
        //}
        UserBook userBook = getOrCreateUserBook(user, book);
        timerRepository.turnOffReadingTimers(userBook);
        Timer timer = Timer.builder()
                .userBook(userBook)
                .readTime(BigInteger.valueOf(0))
                .isReading(true)
                .build();
        timerRepository.save(timer);
        return ResponseEntity.ok(ApiResponse.builder()
                .check(true)
                .information(StartTimerIdRes.builder()
                        .timerId(timer.getTimerId())
                        .build())
                .build());
    }

    private UserBook getOrCreateUserBook(User user, Book book) {
        return userBookRepository.findByUserAndBook(user, book)
                .orElseGet(() -> {
                    UserBook newUserBook = UserBook.builder()
                            .user(user)
                            .book(book)
                            .bookStatus(BookStatus.BEFORE_READ)
                            .build();
                    return userBookRepository.save(newUserBook);
                });
    }

    // 타이머 저장
    @Transactional
    public ResponseEntity<?> updateTimer(UserPrincipal userPrincipal, Long timerId, UpdateTimerReq updateTimerReq) {
        User user = validUserById(userPrincipal.getId());
        Timer timer = validTimerById(timerId);
        UserBook userBook = timer.getUserBook();
        plusTotalReadTime(userBook, updateTimerReq.getTime());
        long timerCount = timerRepository.countByUserBook(userBook);
        if (timerCount >= 10) {
            deleteOldestTimer(userBook, timerCount);
        }
        DefaultAssert.isTrue(timer.isReading(), "타이머를 시작하지 않았습니다.");
        timer.updateReadTime(updateTimerReq.getTime());
        timer.updateIsReading(false);
        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information("타이머가 저장되었습니다.")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private void deleteOldestTimer(UserBook userBook, long timerCount) {
        while (timerCount > 9) {
            Timer oldestTimer = timerRepository.findTop1ByUserBookOrderByCreatedAtAsc(userBook);
            timerRepository.delete(oldestTimer);
            timerCount--;
        }
    }

    public void plusTotalReadTime(UserBook userBook, BigInteger additionalTime) {
        userBook.addTotalReadTime(additionalTime);
    }

    public String convertBigIntegerToString(BigInteger time) {
         if (time == null) {
            return null;
        }
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
        User user = validUserById(userPrincipal.getId());
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
        List<Timer> timerList = timerRepository.findByUserBookAndIsReadingOrderByCreatedAtDesc(userBook, false);
        List<TimerRecordRes> timerRecordRes = timerList.stream()
                .map(timer -> TimerRecordRes.builder()
                        .timerId(timer.getTimerId())
                        .date(timer.getCreatedAt().toLocalDate())
                        .time(timer.getCreatedAt().toLocalTime())
                        .readTime(convertBigIntegerToString(timer.getReadTime()))
                        .build())
                .toList();
        Optional<Timer> timer = timerRepository.findByUserBookAndIsReading(userBook, true);
        boolean isReading = timer.isPresent();
        Long timerId = isReading ? timer.get().getTimerId() : null;
        TimerRes timerRes = TimerRes.builder()
                .reading(isReading)
                .timerId(timerId)
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

    private Timer validTimerById(Long timerId) {
        Optional<Timer> timerOptional = timerRepository.findById(timerId);
        DefaultAssert.isTrue(timerOptional.isPresent(), "타이머가 존재하지 않습니다.");
        return timerOptional.get();
    }

    private User validUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isTrue(userOptional.isPresent(), "유효한 사용자가 아닙니다.");
        return userOptional.get();
    }

    public List<Timer> getTimerListByDate(User user, LocalDate localDate) {
        return timerRepository.findByUserAndCreatedAt(user, localDate);
    }

    public String sumTotalReadTime(List<Timer> timerList) {
        if (timerList == null || timerList.isEmpty()) {
            return "00:00:00"; // 기본값 반환
        }

        BigInteger totalTime = timerList.stream()
                .map(timer -> Optional.ofNullable(timer.getReadTime()).orElse(BigInteger.ZERO)) // null 값 방지
                .reduce(BigInteger.ZERO, BigInteger::add);

        return convertBigIntegerToString(totalTime);
    }
}
