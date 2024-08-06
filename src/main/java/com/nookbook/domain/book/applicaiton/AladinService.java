package com.nookbook.domain.book.applicaiton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookbook.domain.book.dto.response.BestSellerRes;
import com.nookbook.domain.book.dto.response.SearchRes;
import com.nookbook.domain.keyword.application.KeywordService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AladinService {

    private final UserRepository userRepository;
    private final KeywordService keywordService;

    // ttb 키 value로
    @Value("${aladin.ttb.key}")
    private String KEY;

    @Value("${aladin.search-url}")
    private String SEARCH_URL;

    @Value("${aladin.list-url}")
    private String LIST_URL;

    @Value("${aladin.find-url}")
    private String FIND_URL;

    // 검색
    @Transactional
    public ResponseEntity<?> searchBooks(UserPrincipal userPrincipal, String keyword, int page) {
        User user = validUserById(userPrincipal.getId());
        // 검색 키워드 저장
        keywordService.saveKeyword(user, keyword);

        RestTemplate restTemplate = new RestTemplate();
        // 기본 헤더 설정 (필요에 따라)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        // 요청 URL 구성
        URI requestUrl = UriComponentsBuilder
                .fromUriString(SEARCH_URL)
                .queryParam("ttbkey", KEY)
                .queryParam("Query", keyword)
                .queryParam("QueryType", "Keyword")
                .queryParam("QueryType", "Publisher")
                .queryParam("start", page)
                .queryParam("MaxResults", 10)
                .queryParam("output", "JS")
                .queryParam("Version", 20131101)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        // API 호출 및 응답 처리
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class);
        String responseBody = responseEntity.getBody();

        // JSON 파싱 및 DTO 변환
        SearchRes searchRes = convertToSearchRes(responseBody);

        return ResponseEntity.ok(searchRes);
    }

    private SearchRes convertToSearchRes(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 SearchRes 객체로 변환
            return objectMapper.readValue(json, SearchRes.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new SearchRes(); // 오류 발생 시 빈 객체 반환
        }
    }

    private User validUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isTrue(userOptional.isPresent(), "유효한 사용자가 아닙니다.");
        return userOptional.get();
    }

    // 베스트셀러 + 카테고리
    // 종합(0), 소설(1), 경제/경영(170), 자기계발(336), 시(50940), 에세이(55889), 인문/교양(656), 취미/실용(55890), 매거진(2913)
    public ResponseEntity<?> getBestSellerByCategory(int page, int category, int size) {
        RestTemplate restTemplate = new RestTemplate();
        // 기본 헤더 설정 (필요에 따라)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        // 요청 URL 구성
        URI requestUrl = UriComponentsBuilder
                .fromUriString(LIST_URL)
                .queryParam("ttbkey", KEY)
                .queryParam("QueryType", "Bestseller")
                .queryParam("SearchTarget", "Book")
                .queryParam("start", page)
                .queryParam("MaxResults", size)
                .queryParam("CategoryId", category)
                .queryParam("output", "JS")
                .queryParam("Version", 20131101)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        // API 호출 및 응답 처리
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class);
        String responseBody = responseEntity.getBody();

        // JSON 파싱 및 DTO 변환
        BestSellerRes bestSellerRes = convertToBestSellerRes(responseBody);

        return ResponseEntity.ok(bestSellerRes);
    }

    private BestSellerRes convertToBestSellerRes(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 BestSellerRes 객체로 변환
            return objectMapper.readValue(json, BestSellerRes.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new BestSellerRes(); // 오류 발생 시 빈 객체 반환
        }
    }

}
