package com.nookbook.domain.timer.domain;

import com.nookbook.domain.common.BaseEntity;
import com.nookbook.domain.user_book.domain.UserBook;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Entity
@Table(name="Timer")
@NoArgsConstructor
@Getter
public class Timer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="timer_id", updatable = false, nullable = false, unique = true)
    private Long timerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_book_id")
    private UserBook userBook;

    private BigInteger readTime;

    @Column(name = "is_reading")
    private boolean isReading;

    @Builder
    public Timer(UserBook userBook, BigInteger readTime, boolean isReading) {
        this.userBook = userBook;
        this.readTime = readTime;
        this.isReading = isReading;
    }

    public void updateReadTime(BigInteger readTime) {
        this.readTime = readTime;
    }

    public void updateIsReading(boolean isReading) {
        this.isReading = isReading;
    }

}
