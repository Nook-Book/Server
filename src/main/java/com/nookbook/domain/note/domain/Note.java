package com.nookbook.domain.note.domain;

import com.nookbook.domain.common.BaseEntity;
import com.nookbook.domain.user_book.domain.UserBook;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Note")
@NoArgsConstructor
@Getter
public class Note extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="note_id", updatable = false, nullable = false, unique = true)
    private Long noteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_book_id")
    private UserBook userBook;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder
    public Note(UserBook userBook, String title, String content) {
        this.userBook = userBook;
        this.title = title;
        this.content = content;
    }
}
