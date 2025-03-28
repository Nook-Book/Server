package com.nookbook.domain.alarm.domain.repository;

import com.nookbook.domain.alarm.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

}
