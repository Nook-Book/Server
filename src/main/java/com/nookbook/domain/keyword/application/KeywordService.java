package com.nookbook.domain.keyword.application;

import com.nookbook.domain.book.dto.response.KeywordRes;
import com.nookbook.domain.keyword.domain.Keyword;
import com.nookbook.domain.keyword.domain.repository.KeywordRepository;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import com.nookbook.global.payload.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;

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
    public ResponseEntity<?> getKeywords(UserPrincipal userPrincipal) {
        User user = validUserById(userPrincipal.getId());
        List<Keyword> findKeywords = keywordRepository.findByUser(user);
        findKeywords.sort(Comparator.comparing(Keyword::getCreatedAt));

        List<KeywordRes> keywords = findKeywords.stream()
                .map(keyword -> new KeywordRes(keyword.getKeywordId(), keyword.getContent()))
                .collect(Collectors.toList());

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(keywords)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 키워드 삭제
    @Transactional
    public ResponseEntity<?> deleteKeyword(UserPrincipal userPrincipal, Long keywordId) {
        User user = validUserById(userPrincipal.getId());
        Keyword findKeyword = keywordRepository.findByUserAndKeywordId(user, keywordId);

        DefaultAssert.isTrue(user == findKeyword.getUser(), "해당 검색어에 대한 권한이 없습니다.");
        keywordRepository.delete(findKeyword);

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(Message.builder().message("검색어가 삭제되었습니다.").build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private User validUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isTrue(userOptional.isPresent(), "유효한 사용자가 아닙니다.");
        return userOptional.get();
    }
}
