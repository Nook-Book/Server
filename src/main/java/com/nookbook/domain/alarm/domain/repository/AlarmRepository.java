package com.nookbook.domain.alarm.domain.repository;

import com.nookbook.domain.alarm.domain.Alarm;
import com.nookbook.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Page<Alarm> findByUserAndCreatedAtAfter(User user, LocalDateTime weekAgo, Pageable pageable);
}
