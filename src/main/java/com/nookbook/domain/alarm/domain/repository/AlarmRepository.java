package com.nookbook.domain.alarm.domain.repository;

import com.nookbook.domain.alarm.domain.Alarm;
import com.nookbook.domain.alarm.domain.AlarmType;
import com.nookbook.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Page<Alarm> findByUserAndCreatedAtAfter(User user, LocalDateTime weekAgo, Pageable pageable);

    List<Alarm> findByUserAndAlarmTypeAndSenderId(User receiver, AlarmType alarmType, Long userId);

    List<Alarm> findTopByUserAndSenderIdAndAlarmTypeOrderByCreatedAtDesc(User target, Long userId, AlarmType alarmType);

    // 읽지 않음 상태의 알림들 일괄적 읽음 처리
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Alarm a SET a.isRead = true WHERE a.user = :user AND a.isRead = false")
    void markAllAsRead(@Param("user") User user);
}
