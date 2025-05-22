package com.nookbook.domain.collection.exception;

import com.nookbook.global.exception.AuthorizedException;
import com.nookbook.global.payload.ErrorCode;

public class CollectionNotAuthorizedException extends AuthorizedException {

    public CollectionNotAuthorizedException(){
        super(ErrorCode.COLLECTION_NOT_AUTHORIZED);
    }
}
