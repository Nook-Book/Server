package com.nookbook.domain.collection.domain.repository;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.collection.domain.Collection;
import com.nookbook.domain.collection.domain.CollectionBook;
import com.nookbook.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionBookRepository extends JpaRepository<CollectionBook, Long> {

    CollectionBook findByCollectionAndBook(Collection collection, Book book);

    boolean existsByCollectionUserAndBook(User user, Book book);

    @Query(
            "select cb from CollectionBook cb where cb.collection.collectionId = :collectionId and cb.book.bookId = :bookId")
    Optional<CollectionBook> findByCollectionIdAndBookId(Long collectionId, Long bookId);

    List<CollectionBook> findByCollectionUserAndBook(User user, Book book);

}
