package com.nookbook.domain.book.applicaiton;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.book.dto.response.*;
import com.nookbook.domain.collection.domain.CollectionBook;
import com.nookbook.domain.collection.domain.repository.CollectionBookRepository;
import com.nookbook.domain.keyword.application.KeywordService;
import com.nookbook.domain.note.domain.repository.NoteRepository;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final NoteRepository noteRepository;
    private final UserBookRepository userBookRepository;
    private final CollectionBookRepository collectionBookRepository;

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
                .queryParam("Cover", "Big")
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
                .queryParam("Cover", "Big")
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

    // 상세 조회
    @Transactional
    public ResponseEntity<?> getBookDetail(UserPrincipal userPrincipal, String isbn13) {
        User user = validUserById(userPrincipal.getId());

        BookStatus bookStatus = BookStatus.BEFORE_READ;
        boolean isStoredCollection = false;
        boolean hasNote = false;
        List<Long> ids = new ArrayList<>();

        BookDetailRes bookDetailRes;
        Book book;
        if (!bookRepository.existsByIsbn(isbn13)) {
            bookDetailRes = getBookInfoByISBN(isbn13);
            book = saveBookInfo(bookDetailRes);
        } else {
            book = validBookByIsbn13(isbn13);
            Optional<UserBook> userBookOptional = userBookRepository.findByUserAndBook(user, book);
            if (userBookOptional.isPresent()) {
                UserBook userBook = userBookOptional.get();
                // 독서 상태 확인
                bookStatus = userBookOptional.get().getBookStatus();
                // 노트 존재 여부 확인
                hasNote = noteRepository.existsByUserBook(userBook);
            }
            // 컬렉션 저장 여부 확인
            List<CollectionBook> collectionBooks = collectionBookRepository.findByCollectionUserAndBook(user, book);
            isStoredCollection = !collectionBooks.isEmpty();
            if (isStoredCollection) {
                ids = collectionBooks.stream()
                        .map(collectionBook -> collectionBook.getCollection().getCollectionId())
                        .collect(Collectors.toList());
            }
            bookDetailRes = BookDetailRes.builder()
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .cover(book.getImage())
                    .isbn13(isbn13)
                    .page(book.getPage())
                    .pubDate(book.getPublishedDate().toString())
                    .description(book.getInfo())
                    .toc(book.getIdx())
                    .link(book.getLink())
                    .category(book.getCategory())
                    .build();
        }

        BookRes bookRes = BookRes.builder()
                .bookId(book.getBookId())
                .bookStatus(bookStatus)
                .storedCollection(isStoredCollection)
                .collectionIds(ids)
                .hasNote(hasNote)
                .item(bookDetailRes)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(bookRes)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    private Book saveBookInfo(BookDetailRes bookDetailRes) {
        Book book = Book.builder()
                .title(bookDetailRes.getTitle())
                .author(bookDetailRes.getAuthor())
                .publisher(bookDetailRes.getPublisher())
                .image(bookDetailRes.getCover())
                .page(bookDetailRes.getPage())
                .isbn(bookDetailRes.getIsbn13())
                .publishedDate(LocalDate.parse(bookDetailRes.getPubDate()))
                .info(bookDetailRes.getDescription())
                .idx(bookDetailRes.getToc())
                .link(bookDetailRes.getLink())
                .category(bookDetailRes.getCategory()).build();
        bookRepository.save(book);
        return book;
    }

    private BookDetailRes getBookInfoByISBN(String isbn13) {
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
        String responseBody = responseEntity.getBody();

        // JSON 파싱 및 DTO 변환
        return convertToBookDetailRes(responseBody);
    }

    private SearchRes convertToSearchRes(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 SearchRes 객체로 변환
            return objectMapper.readValue(json, SearchRes.class);
        } catch (Exception e) {
            System.err.println("Error converting JSON to SearchRes: " + e.getMessage());
            e.printStackTrace();
            return new SearchRes(); // 오류 발생 시 빈 객체 반환
        }
    }

    private BestSellerRes convertToBestSellerRes(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 BestSellerRes 객체로 변환
            return objectMapper.readValue(json, BestSellerRes.class);
        } catch (Exception e) {
            System.err.println("Error converting JSON to BestSellerRes: " + e.getMessage());
            e.printStackTrace();
            return new BestSellerRes(); // 오류 발생 시 빈 객체 반환
        }
    }

    private BookDetailRes convertToBookDetailRes(String json) {
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
                bookDetailRes.formatCategoryName(bookDetailRes.getCategory());
            }
            // BookDetailRes 객체 반환
            return bookDetailRes;
        } catch (Exception e) {
            System.err.println("Error converting JSON to BookRes: " + e.getMessage());
            e.printStackTrace();
            return new BookDetailRes();
        }
    }

    // 독서 상태 변경
    @Transactional
    public ResponseEntity<?> updateBookStatus(UserPrincipal userPrincipal, Long bookId) {
        User user = validUserById(userPrincipal.getId());

        Optional<Book> bookOptional = bookRepository.findById(bookId);
        DefaultAssert.isTrue(bookOptional.isPresent(), "해당 도서가 존재하지 않습니다.");
        Book book = bookOptional.get();

        UserBook userBook;
        Optional<UserBook> userBookOptional = userBookRepository.findByUserAndBook(user, book);
        // user_book 저장 여부 확인(이전에 읽은 적이 있는지 확인)
        if (userBookOptional.isPresent()) {
            userBook = userBookOptional.get();
            BookStatus bookStatus = userBook.getBookStatus() == BookStatus.READ ? BookStatus.BEFORE_READ : BookStatus.READ;
            // BookStatus만 변경
            userBook.updateBookStatus(bookStatus);
        } else {
            // 읽은 적이 없다면 user_book 저장
            userBook = UserBook.builder()
                    .user(user)
                    .book(book)
                    .bookStatus(BookStatus.READ)
                    .build();
            userBookRepository.save(userBook);
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(userBook.getBookStatus())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 독서 리포트 - 카테고리
    public ResponseEntity<ApiResponse> countReadBooksByCategory(UserPrincipal userPrincipal, Long userId) {
        User targetUser = validUserById(userId);
        List<UserBook> userBooks = userBookRepository.findByUserAndBookStatus(targetUser, BookStatus.READ);
        // 카테고리별로 그룹화하여 카운트
        Map<String, Long> categoryCountMap = userBooks.stream()
                .collect(Collectors.groupingBy(
                        userBook -> userBook.getBook().getCategory(),
                        Collectors.counting()));

        List<MostReadCategoriesRes> readCategoriesRes = categoryCountMap.entrySet().stream()
                .map(entry -> MostReadCategoriesRes.builder()
                        .category(entry.getKey()) // 카테고리명
                        .count(Math.toIntExact(entry.getValue()))  // 카테고리에서 읽은 책 수
                        .build())
                .sorted(Comparator.comparing(MostReadCategoriesRes::getCategory))
                .collect(Collectors.toList());
        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information(readCategoriesRes)
                .build();
        return ResponseEntity.ok(response);
    }

    // 독서 리포트 - 연도별
    public ResponseEntity<ApiResponse> countReadBooksByYear(UserPrincipal userPrincipal, Long userId,  Optional<Integer> yearOptional) {
        User targetUser = validUserById(userId);
        int targetYear = yearOptional.get();
        List<UserBook> userBooks = userBookRepository.findUserBooksByStatusAndYear(targetUser, BookStatus.READ, targetYear);
        // 월별로 그룹화하여 카운트
        Map<Integer, Long> monthCountMap = userBooks.stream()
                .collect(Collectors.groupingBy(
                        userBook -> Integer.valueOf(userBook.getUpdatedAt().format(DateTimeFormatter.ofPattern("MM"))),
                        Collectors.counting()));

        List<BookStatisticsRes> bookStatisticsRes = monthCountMap.entrySet().stream()
                .map(entry -> BookStatisticsRes.builder()
                        .month(entry.getKey()) // 카테고리명
                        .count(Math.toIntExact(entry.getValue()))  // 카테고리에서 읽은 책 수
                        .build())
                .sorted(Comparator.comparingInt(BookStatisticsRes::getMonth))
                .collect(Collectors.toList());
        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information(bookStatisticsRes)
                .build();
        return ResponseEntity.ok(response);
    }

    private User validUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isTrue(userOptional.isPresent(), "유효한 사용자가 아닙니다.");
        return userOptional.get();
    }

    private Book validBookByIsbn13(String isbn13) {
        Optional<Book> bookOptional = bookRepository.findByIsbn(isbn13);
        DefaultAssert.isTrue(bookOptional.isPresent(), "해당 도서가 존재하지 않습니다.");
        return bookOptional.get();
    }



}
