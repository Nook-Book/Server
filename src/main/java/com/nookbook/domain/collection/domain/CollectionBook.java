package com.nookbook.domain.collection.domain;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="CollectionBook")
@NoArgsConstructor
@Getter
public class CollectionBook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_book_id", nullable = false)
    private Long collectionBookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="collection_id")
    private Collection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="book_id")
    private Book book;

}
