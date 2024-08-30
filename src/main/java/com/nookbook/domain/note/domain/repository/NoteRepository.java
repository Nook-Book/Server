package com.nookbook.domain.note.domain.repository;

import com.nookbook.domain.note.domain.Note;
import com.nookbook.domain.user_book.domain.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    int countByUserBook(UserBook userBook);
}
