package com.nookbook.domain.collection.domain;

import com.nookbook.domain.book.domain.Book;
import com.nookbook.domain.common.BaseEntity;
import com.nookbook.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="Collection")
@NoArgsConstructor
@Getter
public class Collection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id", nullable = false)
    private Long collectionId;

    @Column(name="title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollectionBook> collectionBooks;

    @Builder
    public Collection(String title, User user) {
        this.title = title;
        this.user = user;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
