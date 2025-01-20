package com.nookbook.domain.collection.domain.repository;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.collection.domain.Collection;
import com.nookbook.domain.collection.domain.CollectionBook;
import com.nookbook.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionBookRepository extends JpaRepository<CollectionBook, Long> {

    CollectionBook findByCollectionAndBook(Collection collection, Book book);

    List<CollectionBook> findByCollectionUserAndBook(User user, Book book);
}
