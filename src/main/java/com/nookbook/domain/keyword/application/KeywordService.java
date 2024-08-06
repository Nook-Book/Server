package com.nookbook.domain.keyword.application;

import com.nookbook.domain.keyword.domain.Keyword;
import com.nookbook.domain.keyword.domain.repository.KeywordRepository;
import com.nookbook.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {

    private final KeywordRepository keywordRepository;

    @Transactional
    public void saveKeyword(User user, String keyword) {
        // 중복되는 키워드가 있을 경우 삭제
        if (keywordRepository.existsByUserAndContent(user, keyword)) {
            Keyword targetKeyword = keywordRepository.findByUserAndContent(user, keyword);
            keywordRepository.delete(targetKeyword);
        }

        // 새로운 키워드를 저장
        Keyword newKeyword = Keyword.builder()
                .content(keyword)
                .user(user)
                .build();
        keywordRepository.save(newKeyword);

        // 키워드 수를 다시 계산하여 5개 초과 시 가장 오래된 키워드 삭제
        List<Keyword> keywords = keywordRepository.findByUser(user);
        if (keywords.size() > 5) {
            Keyword oldestKeyword = keywordRepository.findFirstByUserOrderByCreatedAtAsc(user);
            if (oldestKeyword != null) {
                keywordRepository.delete(oldestKeyword);
            }
        }
    }

    // 키워드 조회

    // 키워드 삭제
}
