package com.nookbook.domain.user_book.domain.repository;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user_book.domain.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    Optional<UserBook> findByUserAndBook(User user, Book book);
}
