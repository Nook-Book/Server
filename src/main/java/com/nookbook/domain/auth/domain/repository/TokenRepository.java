package com.nookbook.domain.auth.domain.repository;

import com.nookbook.domain.auth.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByEmail(String email);
    Optional<Token> findByRefreshToken(String refreshToken);

}