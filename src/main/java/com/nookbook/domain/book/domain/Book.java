package com.nookbook.domain.book.domain;

import com.nookbook.domain.collection.domain.Collection;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="Book")
@NoArgsConstructor
@Getter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id", nullable = false)
    private Long bookId;

    // 책 제목
    String title;
    // 책 저자
    String author;
    // 페이지 수
    int page;
    // ISBN
    String isbn;
    // 출판일
    String publicatedDate;
    // 책 정보
    String info;
    // 목차
    String index;
    // 카테고리
    String category;
    // 책 표지
    String image;
    // 출판사
    String publisher;

    @OneToMany(mappedBy = "book")
    private List<Collection> collections;

    @Builder
    public Book(String title, String author, int page, String isbn, String publicatedDate, String info, String index, String category, String image, String publisher) {
        this.title = title;
        this.author = author;
        this.page = page;
        this.isbn = isbn;
        this.publicatedDate = publicatedDate;
        this.info = info;
        this.index = index;
        this.category = category;
        this.image = image;
        this.publisher = publisher;
    }
}
