package com.nookbook.domain.note.domain.repository;

import com.nookbook.domain.note.domain.Note;
import com.nookbook.domain.user_book.domain.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    int countByUserBook(UserBook userBook);

    boolean existsByUserBook(UserBook userBook);

    List<Note> findByUserBookOrderByCreatedAtDesc(UserBook userBook);

    List<Note> findByUserBookInOrderByCreatedAtDesc(List<UserBook> userBooks);

    List<Note> findByUserBookInAndLockedOrderByCreatedAtDesc(List<UserBook> userBooks, boolean locked);

    List<Note> findByUserBookAndLockedOrderByCreatedAtDesc(UserBook userBook, boolean locked);
}
