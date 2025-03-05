package com.nookbook.domain.timer.domain.repository;

import com.nookbook.domain.timer.domain.Timer;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user_book.domain.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimerRepository extends JpaRepository<Timer, Long> {
    int countByUserBook(UserBook userBook);

    Timer findTop1ByUserBookOrderByCreatedAtAsc(UserBook userBook);

    List<Timer> findByUserBookAndIsReadingOrderByCreatedAtDesc(UserBook userBook, boolean isReading);

    Optional<Timer> findByUserBookAndIsReading(UserBook userBook, boolean isReading);

    @Modifying
    @Query("UPDATE Timer t SET t.isReading = false WHERE t.userBook = :userBook AND t.isReading = true")
    void turnOffReadingTimers(@Param("userBook") UserBook userBook);

    List<Timer> findByUserBookAndCreatedAtAfter(UserBook userBook, LocalDateTime localDateTime);

    @Query("SELECT t FROM Timer t WHERE t.userBook.user = :user AND FUNCTION('DATE', t.createdAt) = :localDate")
    List<Timer> findByUserAndCreatedAt(User user, LocalDate localDate);
}
