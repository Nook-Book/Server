package com.nookbook.domain.challenge.domain.repository;

import com.nookbook.domain.challenge.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}