package com.nookbook.domain.collection.exception;

import com.nookbook.global.exception.NotFoundException;
import com.nookbook.global.payload.ErrorCode;

public class CollectionNotFoundException extends NotFoundException {
    public CollectionNotFoundException() {
        super(ErrorCode.COLLECTION_NOT_FOUND);
    }
}
