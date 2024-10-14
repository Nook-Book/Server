package com.nookbook.domain.user_book.domain.repository;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user_book.domain.BookStatus;
import com.nookbook.domain.user_book.domain.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    Optional<UserBook> findByUserAndBook(User user, Book book);

    List<UserBook> findByUserAndBookStatus(User user, BookStatus bookStatus);

    @Query("SELECT ub FROM UserBook ub " +
            "WHERE ub.bookStatus = :bookStatus AND ub.user = :user " +
            "AND FUNCTION('YEAR', ub.updatedAt) = :year")
    List<UserBook> findUserBooksByStatusAndYear(User user, BookStatus bookStatus, int year);

}
