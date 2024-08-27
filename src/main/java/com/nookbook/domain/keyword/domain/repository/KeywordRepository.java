package com.nookbook.domain.keyword.domain.repository;

import com.nookbook.domain.keyword.domain.Keyword;
import com.nookbook.domain.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByUser(User user);

    boolean existsByUserAndContent(User user, String keyword);

    Keyword findFirstByUserOrderByCreatedAtAsc(User user);

    Keyword findByUserAndContent(User user, String keyword);

    Keyword findByUserAndKeywordId(User user, Long keywordId);
}
