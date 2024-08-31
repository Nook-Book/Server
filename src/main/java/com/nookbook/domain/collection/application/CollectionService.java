package com.nookbook.domain.collection.application;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.collection.domain.Collection;
import com.nookbook.domain.collection.domain.CollectionBook;
import com.nookbook.domain.collection.domain.CollectionStatus;
import com.nookbook.domain.collection.domain.repository.CollectionBookRepository;
import com.nookbook.domain.collection.domain.repository.CollectionRepository;
import com.nookbook.domain.collection.dto.request.CollectionCreateReq;
import com.nookbook.domain.collection.dto.request.CollectionOrderReq;
import com.nookbook.domain.collection.dto.request.DeleteBookReq;
import com.nookbook.domain.collection.dto.request.UpdateCollectionTitleReq;
import com.nookbook.domain.collection.dto.response.CollectionBooksListDetailRes;
import com.nookbook.domain.collection.dto.response.CollectionBooksListRes;
import com.nookbook.domain.collection.dto.response.CollectionListDetailRes;
import com.nookbook.domain.collection.dto.response.CollectionListRes;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CollectionId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final CollectionBookRepository collectionBookRepository;

    @Transactional
    public ResponseEntity<?> createCollection(UserPrincipal userPrincipal, CollectionCreateReq collectionCreateReq) {
        User user= userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        // 사용자의 마지막 컬렉션 순서를 가져와서 새로운 순서를 지정
        int maxOrderIndex = collectionRepository.findMaxOrderIndexByUser(user).orElse(0);


        Collection collection = Collection.builder()
                .title(collectionCreateReq.getTitle())
                .orderIndex(maxOrderIndex + 1L)
                .user(user)
                .collectionStatus(CollectionStatus.NORMAL)
                .build();

        collectionRepository.save(collection);

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information("컬렉션 생성 완료")
                .build();

        return ResponseEntity.ok(response);
    }

    // CollectionListRes -> CollectionListDetailRes (컬렉션ID, 제목, 커버 리스트) -> CollectionCoverRes(커버 리스트)
    public ResponseEntity<?> getCollectionList(UserPrincipal userPrincipal) {
        User user = userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        List<Collection> collections = collectionRepository.findAllByUser(user);

        List<CollectionListDetailRes> collectionListDetailRes = collections.stream()
                .map(collection -> {
                    // 컬렉션에 속한 도서 중 최근 추가된 4권의 표지 이미지 리스트
                    List<String> coverImages = getTop4BookImagesByCollectionId(collection.getCollectionId());

                    // 컬렉션 각각의 정보
                    return CollectionListDetailRes.builder()
                            .order(collection.getOrderIndex())
                            .collectionStatus(collection.getCollectionStatus())
                            .id(collection.getCollectionId())
                            .title(collection.getTitle())
                            .coverList(coverImages)
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


    public List<String> getTop4BookImagesByCollectionId(Long collectionId) {
        return collectionRepository.findTop4BookImagesByCollectionId(collectionId);
    }

    @Transactional
    public ResponseEntity<?> updateCollectionTitle(UserPrincipal userPrincipal, Long collectionId, UpdateCollectionTitleReq updateCollectionTitleReq) {

        // findCollectionByUserAndCollectionId
        Collection collection = findCollectionByUserAndCollectionId(userPrincipal, collectionId);
        collection.updateTitle(updateCollectionTitleReq.getTitle());

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information("컬렉션 제목 수정 완료")
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getCollectionBooks(UserPrincipal userPrincipal, Long collectionId) {
        Collection collection = findCollectionByUserAndCollectionId(userPrincipal, collectionId);

        // CollectionBook 엔티티에서 Book 객체 추출
        List<Book> books = collection.getCollectionBooks().stream()
                .map(CollectionBook::getBook)
                .toList();

        // CollectionBookListRes, CollectionBookListDetailRes
        List<CollectionBooksListDetailRes> bookResponses = books.stream()
                .map(book -> CollectionBooksListDetailRes.builder()
                        .bookId(book.getBookId())
                        .title(book.getTitle())
                        .cover(book.getImage())
                        .build())
                .toList();

        CollectionBooksListRes collectionBooksListRes = CollectionBooksListRes.builder()
                .totalBooks((long) bookResponses.size())
                .collectionBooksListDetailRes(bookResponses)
                .build();

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information(collectionBooksListRes)
                .build();

        return ResponseEntity.ok(response);
    }



    public Collection findCollectionByUserAndCollectionId(UserPrincipal userPrincipal, Long collectionId) {
        User user = userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("컬렉션을 찾을 수 없습니다."));

        // 사용자가 소유한 컬렉션인지 확인
        if (!collection.getUser().equals(user)) {
            throw new RuntimeException("해당 컬렉션에 대한 접근 권한이 없습니다.");
        }
        return collection;
    }


    @Transactional
    public ResponseEntity<?> addBookToCollection(UserPrincipal userPrincipal, Long collectionId, Long bookId) {
        Collection collection = findCollectionByUserAndCollectionId(userPrincipal, collectionId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("도서를 찾을 수 없습니다."));

        // 만약 collectionBook이 이미 존재한다면 추가하지 않고 메세지 반환
        CollectionBook collectionBookCheck = collectionBookRepository.findByCollectionAndBook(collection, book);
        if (collectionBookCheck != null) {
            ApiResponse response = ApiResponse.builder()
                    .check(false)
                    .information("이미 컬렉션에 추가된 도서입니다.")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
        else{
            CollectionBook collectionBook = CollectionBook.builder()
                    .collection(collection)
                    .book(book)
                    .build();

            collectionBookRepository.save(collectionBook);

            ApiResponse response = ApiResponse.builder()
                    .check(true)
                    .information("컬렉션에 도서 추가 완료")
                    .build();

            return ResponseEntity.ok(response);
        }
    }

    @Transactional
    public ResponseEntity<?> deleteBookFromCollection(UserPrincipal userPrincipal, Long collectionId, DeleteBookReq deleteBookReq) {

        // 컬렉션 검증
        Collection collection = findCollectionByUserAndCollectionId(userPrincipal, collectionId);
        List<Long> bookIds = deleteBookReq.getBookIds();

        // 컬렉션에 속한 도서 중 삭제 요청된 도서를 찾아 삭제
        for (Long bookId : bookIds) {
            CollectionBook collectionBook = collectionBookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("컬렉션 도서를 찾을 수 없습니다."));
            collectionBookRepository.delete(collectionBook);
        }

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information("컬렉션 도서 삭제 완료")
                .build();

        return ResponseEntity.ok(response);
    }


    // 컬렉션 순서 변경
    // 컬렉션 목록의 순서를 모두 요청값으로 받아서 수정
    @Transactional
    public ResponseEntity<?> editCollectionOrder(UserPrincipal userPrincipal, List<CollectionOrderReq> collectionOrderReqList) {
        User user = userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

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

        ApiResponse response = ApiResponse.builder()
                .check(true)
                .information("컬렉션 순서 변경 완료")
                .build();

        return ResponseEntity.ok(response);
    }

}
