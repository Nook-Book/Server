package com.nookbook.domain.collection.domain;

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

    @Column(name = "order_index")
    private Long orderIndex;

    @Enumerated(EnumType.STRING)
    private CollectionStatus collectionStatus;


    @Builder
    public Collection(String title, User user, Long orderIndex, CollectionStatus collectionStatus) {
        this.title = title;
        this.user = user;
        this.orderIndex = orderIndex;
        this.collectionStatus = collectionStatus;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateOrderIndex(Long orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void updateStatus(CollectionStatus collectionStatus) {
        this.collectionStatus = collectionStatus;
    }
}
