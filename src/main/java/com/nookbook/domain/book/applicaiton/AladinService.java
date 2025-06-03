package com.nookbook.domain.book.applicaiton;

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

    @Value("${aladin.list-url}")
    private String LIST_URL;

    @Value("${aladin.find-url}")
    private String FIND_URL;

    public String callAladinSearchBooks(String keyword, int page) {
        RestTemplate restTemplate = new RestTemplate();
        // 기본 헤더 설정 (필요에 따라)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        URI requestUrl = UriComponentsBuilder
                .fromUriString(SEARCH_URL)
                .queryParam("ttbkey", KEY)
                .queryParam("Query", keyword)
                .queryParam("QueryType", "Keyword")
                .queryParam("QueryType", "Publisher")
                .queryParam("start", page)
                .queryParam("MaxResults", 10)
                .queryParam("Cover", "Big")
                .queryParam("output", "JS")
                .queryParam("Version", 20131101)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        // API 호출 및 응답 처리
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class);
        return responseEntity.getBody();
    }

    // 베스트셀러 + 카테고리
    // 종합(0), 소설(1), 경제/경영(170), 자기계발(336), 시(50940), 에세이(55889), 인문/교양(656), 취미/실용(55890), 매거진(2913)
    public String callAladinBestSellers(int page, int category, int size) {
        RestTemplate restTemplate = new RestTemplate();
        // 기본 헤더 설정 (필요에 따라)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        URI requestUrl = UriComponentsBuilder
                .fromUriString(LIST_URL)
                .queryParam("ttbkey", KEY)
                .queryParam("QueryType", "Bestseller")
                .queryParam("SearchTarget", "Book")
                .queryParam("start", page)
                .queryParam("MaxResults", size)
                .queryParam("Cover", "Big")
                .queryParam("CategoryId", category)
                .queryParam("output", "JS")
                .queryParam("Version", 20131101)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        // API 호출 및 응답 처리
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class);
        return responseEntity.getBody();
    }

    public String callAladinBookDetail(String isbn13) {
        RestTemplate restTemplate = new RestTemplate();
        // 기본 헤더 설정 (필요에 따라)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        // 요청 URL 구성
        URI requestUrl = UriComponentsBuilder
                .fromUriString(FIND_URL)
                .queryParam("ttbkey", KEY)
                .queryParam("ItemId", isbn13)
                .queryParam("itemIdType", "ISBN13")
                .queryParam("Cover", "Big")
                .queryParam("output", "JS")
                .queryParam("Version", 20131101)
                .queryParam("OptResult", "Toc,fulldescription")
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        // API 호출 및 응답 처리
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class);
        return responseEntity.getBody();
    }

}
