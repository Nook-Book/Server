package com.nookbook.domain.book;

import com.nookbook.global.exception.NotFoundException;

public class BookNotFoundException extends NotFoundException {
    public BookNotFoundException() {
        super("B001", "책을 찾을 수 없습니다.");
    }
}
