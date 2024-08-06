package com.nookbook.domain.book.applicaiton;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.book.dto.response.BestSellerRes;
import com.nookbook.domain.book.dto.response.BookDetailRes;
import com.nookbook.domain.book.dto.response.BookRes;
import com.nookbook.domain.book.dto.response.SearchRes;
import com.nookbook.domain.keyword.application.KeywordService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user_book.domain.BookStatus;
import com.nookbook.domain.user_book.domain.UserBook;
import com.nookbook.domain.user_book.domain.repository.UserBookRepository;
import com.nookbook.global.DefaultAssert;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserBookRepository userBookRepository;

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

    // 상세 조회
    public ResponseEntity<?> getBookDetail(UserPrincipal userPrincipal, String isbn13) {
        User user = validUserById(userPrincipal.getId());
        BookStatus bookStatus = BookStatus.BEFORE_READING;
        boolean isStoredCollection = false;

        BookRes bookRes;
        if (bookRepository.existsByIsbn(isbn13)) {
            Book book = bookRepository.findByIsbn(isbn13);

            Optional<UserBook> userBookOptional = userBookRepository.findByUserAndBook(user, book);
            if (userBookOptional.isPresent()) {
                bookStatus = userBookOptional.get().getBookStatus();
            }
            // 컬렉션 저장 여부 확인
            // Optional<Collection> collectionOptional = collectionRepository.findByUserAndIsbn(user, isbn13);
            // if (collectionOptional.isPresent()) {
            //     isStoredCollection = true; // 컬렉션에 저장된 상태
            // }

            BookDetailRes bookDetailRes = BookDetailRes.builder()
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .cover(book.getImage())
                    .isbn13(isbn13)
                    .page(book.getPage())
                    .pubDate(book.getPublishedDate().toString())
                    .description(book.getInfo())
                    .toc(book.getIdx())
                    .link(book.getLink())
                    .build();

            bookRes = BookRes.builder()
                    .bookStatus(bookStatus)
                    .storedCollection(isStoredCollection)
                    .item(bookDetailRes)
                    .build();
        } else {
            bookRes = getBookInfoByISBN(isbn13);
            // 추가로 bookStatus, storedCollection 설정
            bookRes.setBookStatus(bookStatus);
            bookRes.setStoredCollection(isStoredCollection);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(bookRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private BookRes getBookInfoByISBN(String isbn13) {
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
                .queryParam("output", "JS")
                .queryParam("Version", 20131101)
                .queryParam("OptResult", "Toc,fulldescription")
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        // API 호출 및 응답 처리
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class);
        String responseBody = responseEntity.getBody();

        // JSON 파싱 및 DTO 변환
        return convertToBookRes(responseBody);
    }

    private BookRes convertToBookRes(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 Map 형태로 변환
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});

            // "item" 배열을 추출
            List<Map<String, Object>> items = (List<Map<String, Object>>) map.get("item");

            // 첫 번째 아이템을 BookDetailRes로 변환
            BookDetailRes bookDetailRes = null;
            if (items != null && !items.isEmpty()) {
                Map<String, Object> item = items.get(0);
                bookDetailRes = objectMapper.convertValue(item, BookDetailRes.class);

                // subInfo를 추출하고 itemPage 값을 설정
                Map<String, Object> subInfo = (Map<String, Object>) item.get("subInfo");
                if (subInfo != null) {
                    int itemPage = (Integer) subInfo.get("itemPage");
                    String toc = (String) subInfo.get("toc");
                    bookDetailRes.setPage(itemPage);
                    bookDetailRes.setToc(toc);
                }
            }
            // BookRes 객체 생성
            return BookRes.builder()
                    .item(bookDetailRes)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return new BookRes(); // 오류 발생 시 빈 객체 반환
        }
    }



}
