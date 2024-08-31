package com.nookbook.domain.collection.domain.repository;

import com.nookbook.domain.collection.domain.Collection;
import com.nookbook.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findAllByUser(User user);

    // 수정일 기준으로 정렬
    @Query(value = "SELECT b.image FROM collection_book cb " +
            "JOIN book b ON cb.book_id = b.book_id " +
            "WHERE cb.collection_id = :collectionId " +
            "ORDER BY cb.updated_at DESC " +
            "LIMIT 4", nativeQuery = true)

    List<String> findTop4BookImagesByCollectionId(@Param("collectionId") Long collectionId);

    Optional<Integer> findMaxOrderIndexByUser(User user);

    @Modifying
    @Query("UPDATE Collection c SET c.orderIndex = c.orderIndex - 1 WHERE c.user = :user AND c.orderIndex BETWEEN :start AND :end")
    void decrementOrderIndexBetween(@Param("user") User user, @Param("start") Long start, @Param("end") Long end);

    @Modifying
    @Query("UPDATE Collection c SET c.orderIndex = c.orderIndex + 1 WHERE c.user = :user AND c.orderIndex BETWEEN :start AND :end")
    void incrementOrderIndexBetween(@Param("user") User user, @Param("start") Long start, @Param("end") Long end);
}