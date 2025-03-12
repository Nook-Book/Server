package com.nookbook.domain.user_book.domain.repository;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.timer.domain.Timer;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user_book.domain.BookStatus;
import com.nookbook.domain.user_book.domain.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    //가장 최근에 수정된 UserBook
    UserBook findFirstByUserOrderByUpdatedAtDesc(User user);

    List<UserBook> findByUser(User user);

    @Query("SELECT ub FROM UserBook ub JOIN ub.book b WHERE ub.user = :user AND b.title LIKE %:keyword%")
    List<UserBook> findByUserAndBookTitleLike(@Param("user") User user, @Param("keyword") String keyword);

    Optional<UserBook> findByUserAndCreatedAtAfter(User user, LocalDateTime localDateTime);

    @Query("SELECT ub FROM UserBook ub WHERE ub.user = :user AND FUNCTION('DATE', ub.createdAt) = :localDate")
    List<UserBook> findUserBookListByDate(User user, LocalDate localDate);
}
