package com.nookbook.domain.timer.domain.repository;

import com.nookbook.domain.timer.domain.Timer;
import com.nookbook.domain.user_book.domain.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimerRepository extends JpaRepository<Timer, Long> {
    int countByUserBook(UserBook userBook);

    Timer findTop1ByUserBookOrderByCreatedAtAsc(UserBook userBook);

    List<Timer> findByUserBookOrderByCreatedAtDesc(UserBook userBook);
}
