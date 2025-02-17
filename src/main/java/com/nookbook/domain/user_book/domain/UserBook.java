package com.nookbook.domain.user_book.domain;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.common.BaseEntity;
import com.nookbook.domain.timer.domain.Timer;
import com.nookbook.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="User_Book")
@NoArgsConstructor
@Getter
public class UserBook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_book_id", updatable = false, nullable = false, unique = true)
    private Long userBookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @OneToMany
    private List<Timer> timers;

    // 누적 독서 시간
    private BigInteger totalReadTime = BigInteger.ZERO;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus = BookStatus.BEFORE_READ;

    @Builder
    public UserBook(User user, Book book, BookStatus bookStatus) {
        this.user = user;
        this.book = book;
        this.bookStatus = bookStatus;
    }

    public void updateBookStatus(BookStatus bookStatus) { this.bookStatus = bookStatus; }

    public void updateTotalReadTime(BigInteger totalReadTime) { this.totalReadTime = totalReadTime; }
}
