package com.nookbook.domain.timer.domain.repository;

import com.nookbook.domain.timer.domain.Timer;
import com.nookbook.domain.user_book.domain.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimerRepository extends JpaRepository<Timer, Long> {
    int countByUserBook(UserBook userBook);

    Timer findTop1ByUserBookOrderByCreatedAtAsc(UserBook userBook);

    List<Timer> findByUserBookAndIsReadingOrderByCreatedAtDesc(UserBook userBook, boolean isReading);

    Optional<Timer> findByUserBookAndIsReading(UserBook userBook, boolean isReading);

    List<Timer> findByUserBookAndCreatedAtAfter(UserBook userBook, LocalDateTime localDateTime);
}
