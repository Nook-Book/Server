package com.nookbook.domain.book.applicaiton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookbook.domain.book.dto.response.SearchRes;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AladinService {

    // ttb 키 value로
    @Value("${aladin.ttb.key}")
    private String KEY;

    @Value("${aladin.search-url}")
    private String SEARCH_URL;

    // 검색
    public ResponseEntity<?> searchBooks(String keyword, int page) {
        RestTemplate restTemplate = new RestTemplate();
        // 기본 헤더 설정 (필요에 따라)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        // 요청 URL 구성
        URI searchUrl = UriComponentsBuilder
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
        ResponseEntity<String> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, httpEntity, String.class);
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


    // 카테고리별
    // 베스트셀러
}
