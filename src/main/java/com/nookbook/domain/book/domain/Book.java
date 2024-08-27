package com.nookbook.domain.book.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="Book")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="book_id", updatable = false, nullable = false, unique = true)
    private Long bookId;

    @Column(name="title")
    private String title;

    @Column(name="author")
    private String author;

    @Column(name="page")
    private int page;

    @Column(name="isbn", updatable = false, nullable = false, unique = true)
    private String isbn;

    @Column(name="publishedDate")
    private LocalDate publishedDate;

    @Lob
    @Column(name="info")
    private String info;

    @Lob
    @Column(name="idx")
    private String idx;

    @Column(name="category")
    private String category;

    @Column(name="image")
    private String image;

    @Column(name="link")
    private String link;

    @Builder
    public Book(String title, String author, int page, String isbn, LocalDate publishedDate, String info, String idx, String category, String image, String link) {
        this.title = title;
        this.author = author;
        this.page = page;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
        this.info = info;
        this.idx = idx;
        this.category = category;
        this.image = image;
        this.link = link;
    }


}
