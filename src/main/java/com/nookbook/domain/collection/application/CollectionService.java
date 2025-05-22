package com.nookbook.domain.collection.application;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.book.exception.BookNotFoundException;
import com.nookbook.domain.collection.domain.Collection;
import com.nookbook.domain.collection.domain.CollectionBook;
import com.nookbook.domain.collection.domain.CollectionStatus;
import com.nookbook.domain.collection.domain.repository.CollectionBookRepository;
import com.nookbook.domain.collection.domain.repository.CollectionRepository;
import com.nookbook.domain.collection.dto.request.*;
import com.nookbook.domain.collection.dto.response.*;
import com.nookbook.domain.collection.exception.BookAlreadyExistsInCollectionException;
import com.nookbook.domain.collection.exception.BookNotInCollectionException;
import com.nookbook.domain.collection.exception.CollectionNotAuthorizedException;
import com.nookbook.domain.collection.exception.CollectionNotFoundException;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.exception.UserNotFoundException;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final CollectionBookRepository collectionBookRepository;


    // 컬렉션 생성
    @Transactional
    public ResponseEntity<?> createCollection(UserPrincipal userPrincipal, CollectionCreateReq collectionCreateReq) {
        User user = validateUser(userPrincipal);
        // 사용자의 마지막 컬렉션 순서를 가져와서 새로운 순서를 지정
        // 컬렉션 순서는 1부터 시작
        long maxOrderIndex = collectionRepository.findMaxOrderIndexByUser(user).orElse(1L);

        Collection collection = Collection.builder()
                .title(collectionCreateReq.getTitle())
                .orderIndex(maxOrderIndex + 1L)
                .user(user)
                .collectionStatus(CollectionStatus.NORMAL)
                .build();

        collectionRepository.save(collection);

        return buildOkResponse("컬렉션 생성이 완료되었습니다.", true);
    }

    // 컬렉션 리스트 조회
    // CollectionListRes -> CollectionListDetailRes (컬렉션ID, 제목, 커버 리스트) -> CollectionCoverRes(커버 리스트)
    public ResponseEntity<?> getCollectionList(UserPrincipal userPrincipal) {
        User user = validateUser(userPrincipal);
        List<Collection> collections = collectionRepository.findAllByUser(user);

        if(collections.isEmpty()){
            ApiResponse response = ApiResponse.builder()
                    .check(true)
                    .information(CollectionListRes.builder()
                            .totalCollections(0L)
                            .collectionListDetailRes(new ArrayList<>())
                            .build())
                    .build();
            return ResponseEntity.ok(response);
        }

        List<CollectionListDetailRes> collectionListDetailRes = collections.stream()
                .map(collection -> {
                    // 컬렉션에 속한 도서 중 최근 추가된 4권의 표지 이미지 리스트
                    List<String> coverImages = getTop4BookImagesByCollectionId(collection.getCollectionId());

                    // 컬렉션 각각의 정보
                    return CollectionListDetailRes.builder()
                            .order(collection.getOrderIndex())
                            .collectionStatus(collection.getCollectionStatus())
                            .collectionId(collection.getCollectionId())
                            .collectionTitle(collection.getTitle())
                            .totalBooks(collection.getCollectionBooks().size()) // 컬렉션 내 도서 수
                            .collectionBooksCoverList(coverImages)
                            .build();
                })
                .toList();

        // 컬렉션 정보를 담은 CollectionListRes 객체 생성
        // 순서 orderIndex 필드값에 따라 오름차순으로 정렬
        CollectionListRes collectionListRes = CollectionListRes.builder()
                .totalCollections((long) collectionListDetailRes.size())
                .collectionListDetailRes(collectionListDetailRes.stream()
                        .sorted((a, b) -> (int) (a.getOrder() - b.getOrder()))
                        .collect(Collectors.toList()))
                .build();

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information(collectionListRes)
                .build();
        return ResponseEntity.ok(response);
    }


    // 컬렉션의 최근 추가된 4권의 표지 이미지 리스트 조회
    public List<String> getTop4BookImagesByCollectionId(Long collectionId) {
        return collectionRepository.findTop4BookImagesByCollectionId(collectionId);
    }

    // 컬렉션 제목 수정
    @Transactional
    public ResponseEntity<?> updateCollectionTitle(UserPrincipal userPrincipal, Long collectionId, UpdateCollectionTitleReq updateCollectionTitleReq) {

        // findCollectionByUserAndCollectionId
        Collection collection = findCollectionByUserAndCollectionId(userPrincipal, collectionId);
        collection.updateTitle(updateCollectionTitleReq.getTitle());

        return buildOkResponse("컬렉션 제목 수정이 완료되었습니다.", true);
    }


    // 컬렉션 내의 도서 목록 조회
    public ResponseEntity<?> getCollectionBooks(UserPrincipal userPrincipal, Long collectionId) {
        Collection collection = findCollectionByUserAndCollectionId(userPrincipal, collectionId);

        // 컬렉션 내의 도서 목록 조회
        CollectionBooksListRes collectionBooksListRes = getCollectionBooksListDetailRes(collection);

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information(collectionBooksListRes)
                .build();

        return ResponseEntity.ok(response);
    }

    // 컬렉션 내의 도서 목록 조회 매소드 분리
    public CollectionBooksListRes getCollectionBooksListDetailRes(Collection collection) {
        // CollectionBook 엔티티에서 Book 객체 추출
        List<Book> books = collection.getCollectionBooks().stream()
                .map(CollectionBook::getBook)
                .toList();

        // CollectionBookListRes, CollectionBookListDetailRes
        List<CollectionBooksListDetailRes> bookResponses = books.stream()
                .map(book -> CollectionBooksListDetailRes.builder()
                        .bookId(book.getBookId())
                        .isbn(book.getIsbn())
                        .title(book.getTitle())
                        .author(book.getAuthor())
                        .cover(book.getImage())
                        .build())
                .toList();

        CollectionBooksListRes collectionBooksListRes = CollectionBooksListRes.builder()
                .totalBooks((long) bookResponses.size())
                .collectionBooksListDetailRes(bookResponses)
                .build();

        return collectionBooksListRes;
    }



    // 컬렉션 소유자 검증
    public Collection findCollectionByUserAndCollectionId(UserPrincipal userPrincipal, Long collectionId) {
        User user = validateUser(userPrincipal);

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("컬렉션을 찾을 수 없습니다."));

        // 사용자가 소유한 컬렉션인지 확인
        if (!collection.getUser().equals(user)) {
            throw new RuntimeException("해당 컬렉션에 대한 접근 권한이 없습니다.");
        }
        return collection;
    }


    // 컬렉션에 도서 추가
    @Transactional
    public ResponseEntity<?> addBookToCollection(UserPrincipal userPrincipal, Long collectionId, Long bookId) {
        Collection collection = validateUserCollection(userPrincipal, collectionId);
        Book book = findBook(bookId);

        // 이미 존재하는 경우: 200 OK + check: false
        if (isBookAlreadyInCollection(collection, book)) {
            return buildOkResponse("이미 컬렉션에 추가된 도서입니다.", false);
        }

        // 새로 추가
        addBookToCollection(collection, book);
        return buildOkResponse("컬렉션에 도서 추가 완료", true);
    }

    private Collection validateUserCollection(UserPrincipal userPrincipal, Long collectionId) {
        return findCollectionByUserAndCollectionId(userPrincipal, collectionId);
    }

    // 도서 조회
    private Book findBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);
    }

    private boolean isBookAlreadyInCollection(Collection collection, Book book) {
        return collectionBookRepository.findByCollectionAndBook(collection, book) != null;
    }

    private void addBookToCollection(Collection collection, Book book) {
        CollectionBook collectionBook = CollectionBook.builder()
                .collection(collection)
                .book(book)
                .build();
        collectionBookRepository.save(collectionBook);
    }



    // 컬렉션에서 도서 삭제
    @Transactional
    public ResponseEntity<?> deleteBookFromCollection(UserPrincipal userPrincipal, Long collectionId, DeleteBookReq deleteBookReq) {
        Collection collection = validateUserCollection(userPrincipal, collectionId);
        List<Long> bookIds = deleteBookReq.getBookIds();

        deleteBooksFromCollection(collectionId, bookIds);

        return buildOkResponse("컬렉션 도서 삭제가 완료되었습니다.", true);
    }


    private void deleteBooksFromCollection(Long collectionId, List<Long> bookIds) {
        for (Long bookId : bookIds) {
            CollectionBook collectionBook = collectionBookRepository.findByCollectionIdAndBookId(collectionId, bookId)
                    .orElseThrow(() -> new RuntimeException("컬렉션 도서를 찾을 수 없습니다."));

            collectionBookRepository.delete(collectionBook);
        }
    }


    // 컬렉션 순서 변경
    // 컬렉션 목록의 순서를 모두 요청값으로 받아서 수정
    @Transactional
    public ResponseEntity<?> editCollectionOrder(UserPrincipal userPrincipal, List<CollectionOrderReq> collectionOrderReqList) {
        validateUser(userPrincipal);

        // 컬렉션 순서 변경
        // 1. 요청값의 collectionId를 통해 컬렉션 객체를 찾음
        // 2. 컬렉션 객체의 orderIndex를 요청값의 order로 변경 /
        // 3. 순서 top 4의 컬렉션은 MAIN으로 변경, 나머지는 NORMAL로 변경

        for(int i = 0 ; i < collectionOrderReqList.size() ; i++){
            CollectionOrderReq collectionOrderReq = collectionOrderReqList.get(i);
            Collection collection = collectionRepository.findById(collectionOrderReq.getCollectionId())
                    .orElseThrow(() -> new RuntimeException("컬렉션을 찾을 수 없습니다."));

            Long idx = collectionOrderReq.getOrder();
            int statusNum = collectionOrderReq.getStatus(); // 1 : MAIN, 0 : NORMAL

            // 컬렉션 순서 변경
            collection.updateOrderIndex(idx);

            // 컬렉션 상태 변경
            if(statusNum == 1){
                collection.updateStatus(CollectionStatus.MAIN);
            }
            else{
                collection.updateStatus(CollectionStatus.NORMAL);
            }
        }

        return buildOkResponse("컬렉션 순서 변경이 완료되었습니다.", true);
    }


    // 현재 컬렉션들의 정보 상세 조회
    public ResponseEntity<?> getCurrentCollectionBooks(UserPrincipal userPrincipal) {
        User user = validateUser(userPrincipal);

        // 유저의 컬렉션 중 CollectionStatus.MAIN인 컬렉션을 찾아 도서 목록 조회
        List<Collection> mainCollections = collectionRepository.findAllByUserAndCollectionStatus(user, CollectionStatus.MAIN);

        // MAIN 컬렉션이 없다면 빈 컬렉션 리스트 반환

        if(mainCollections.isEmpty()){
            ApiResponse response = ApiResponse.builder()
                    .check(true)
                    .information(MainCollectionListRes.builder()
                            .totalCollections(0)
                            .mainCollectionListDetailRes(new ArrayList<>())
                            .build())
                    .build();
            return ResponseEntity.ok(response);
        }

        // 순서대로 정렬
        mainCollections.sort((a, b) -> (int) (a.getOrderIndex() - b.getOrderIndex()));

        // id만 순서대로 저장
        ArrayList<Long> collectionIdList = new ArrayList<>();
        for(Collection collection : mainCollections){
            collectionIdList.add(collection.getCollectionId());
        }

        List<MainCollectionListDetailRes> mainCollectionListDetailRes = mainCollections.stream()
                .map(collection -> {
                    // 컬렉션에 속한 도서 중 최근 추가된 4권의 표지 이미지 리스트
                    List<Book> books = collection.getCollectionBooks().stream()
                            .map(CollectionBook::getBook)
                            .toList();
                    // 특정 컬렉션 내 도서 정보
                    List<CollectionBooksListDetailRes> bookInfo = books.stream()
                            .map(book -> CollectionBooksListDetailRes.builder()
                                    .bookId(book.getBookId())
                                    .isbn(book.getIsbn())
                                    .title(book.getTitle())
                                    .cover(book.getImage())
                                    .build())
                            .toList();
                    // 컬렉션 각각의 정보
                    return MainCollectionListDetailRes.builder()
                            .order(collection.getOrderIndex())
                            .collectionStatus(collection.getCollectionStatus())
                            .collectionId(collection.getCollectionId())
                            .collectionTitle(collection.getTitle())
                            .collectionBooksListDetailRes(bookInfo)
                            .build();
                })
                .toList();


        MainCollectionListRes mainCollectionListRes = MainCollectionListRes.builder()
                .totalCollections(mainCollectionListDetailRes.size())
                .mainCollectionListDetailRes(mainCollectionListDetailRes)
                .build();


        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information(mainCollectionListRes)
                .build();

        return ResponseEntity.ok(response);

    }

    // 컬렉션 삭제
    @Transactional
    public ResponseEntity<?> deleteCollection(UserPrincipal userPrincipal, Long collectionId) {
        User user = validateUser(userPrincipal);
        Collection collection = existCollection(collectionId);
        isCollectionOwner(user, collection);

        collectionRepository.delete(collection);
        // 컬렉션 삭제 시 컬렉션들의 orderIndex 재정렬
        Collection.reorderCollectionOrderIdx(collectionRepository.findAllByUser(user));

        return buildOkResponse("컬렉션 삭제가 완료되었습니다.", true);
    }

    // 컬렉션 내 도서 이동
    @Transactional
    public void moveBookToAnotherCollection(UserPrincipal userPrincipal, Long fromCollectionId, Long targetCollectionId, BookIdListReq bookIdListReq) {
        List<Long> bookIds = bookIdListReq.getBookIds();

        // 1. 사용자 소유 컬렉션 검증
        Collection fromCollection = validateUserCollection(userPrincipal, fromCollectionId);
        Collection toCollection = validateUserCollection(userPrincipal, targetCollectionId);

        // 2. 각 도서에 대해 이동 처리
        for (Long bookId : bookIds) {
            moveSingleBook(fromCollection, toCollection, bookId);
        }
    }

    // 개별 도서 이동 처리 메소드
    private void moveSingleBook(Collection from, Collection to, Long bookId) {
        // 도서 검증
        Book book = findBook(bookId);

        // 1. 원본 컬렉션에 존재하는지 확인
        CollectionBook fromEntry = collectionBookRepository.findByCollectionAndBook(from, book);
        if (fromEntry == null) {
            throw new BookNotInCollectionException();
        }

        // 2. 삭제 후 대상 컬렉션 중복 여부 확인
        collectionBookRepository.delete(fromEntry);
        if (collectionBookRepository.findByCollectionAndBook(to, book) != null) {
            throw new BookAlreadyExistsInCollectionException();
        }

        // 3. 도서 추가
        addBookToCollection(to, book);
    }




    // 사용자 검증 메서드
    private User validateUser(UserPrincipal userPrincipal) {
        return userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(UserNotFoundException::new);
    }

    // 컬렉션 검증
    private Collection existCollection(Long collectionId) {
        return collectionRepository.findById(collectionId)
                .orElseThrow(CollectionNotFoundException::new);
    }

    // 컬렉션 소유 검증
    private void isCollectionOwner(User user, Collection collection) {
        if (!collection.getUser().equals(user)) {
            throw new CollectionNotAuthorizedException();
        }
    }

    // 응답 빌더 메서드
    private ResponseEntity<ApiResponse> buildOkResponse(String message, boolean check) {
        ApiResponse response = ApiResponse.builder()
                .check(check)
                .information(message)
                .build();
        return ResponseEntity.ok(response);
    }

}
