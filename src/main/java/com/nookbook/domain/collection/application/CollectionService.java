package com.nookbook.domain.collection.application;

import com.nookbook.domain.book.domain.repository.BookRepository;
import com.nookbook.domain.collection.domain.Collection;
import com.nookbook.domain.collection.domain.repository.CollectionRepository;
import com.nookbook.domain.collection.dto.request.CollectionCreateReq;
import com.nookbook.domain.collection.dto.response.CollectionListDetailRes;
import com.nookbook.domain.collection.dto.response.CollectionListRes;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.global.config.security.token.UserPrincipal;
import com.nookbook.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserService userService;
    private final BookRepository bookRepository;

    @Transactional
    public ResponseEntity<?> createCollection(UserPrincipal userPrincipal, CollectionCreateReq collectionCreateReq) {
        User user= userService.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        Collection collection = Collection.builder()
                .title(collectionCreateReq.getTitle())
                .user(user)
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
                            .id(collection.getCollectionId())
                            .title(collection.getTitle())
                            .coverList(coverImages)
                            .build();
                })
                .toList();

        // 컬렉션 정보를 리스트로 감싸 반환
        CollectionListRes collectionListRes = CollectionListRes.builder()
                .collectionListDetailRes(collectionListDetailRes)
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
}
