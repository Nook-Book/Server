package com.nookbook.domain.book.exception;

import com.nookbook.global.exception.NotFoundException;
import com.nookbook.global.payload.ErrorCode;

public class BookNotFoundException extends NotFoundException {
    public BookNotFoundException() {
        super(ErrorCode.BOOK_NOT_FOUND);
    }
}
