package com.nookbook.domain.collection.exception;

import com.nookbook.global.exception.NotFoundException;
import com.nookbook.global.payload.ErrorCode;

public class BookNotInCollectionException extends NotFoundException {
    public BookNotInCollectionException() {
        super(ErrorCode.BOOK_NOT_IN_COLLECTION);
    }
}
