package com.nookbook.domain.collection.exception;

import com.nookbook.global.exception.AlreadyExistsException;
import com.nookbook.global.payload.ErrorCode;

public class BookAlreadyExistsInCollectionException extends AlreadyExistsException {
    public BookAlreadyExistsInCollectionException() {
        super(ErrorCode.BOOK_ALREADY_EXISTS_IN_COLLECTION);
    }
}
