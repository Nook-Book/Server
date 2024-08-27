package com.nookbook.domain.book.domain.repository;

import com.nookbook.domain.book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    Book findByIsbn(String isbn);
}
