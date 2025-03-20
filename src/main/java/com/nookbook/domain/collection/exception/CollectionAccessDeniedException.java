package com.nookbook.domain.collection.exception;

public class CollectionAccessDeniedException extends RuntimeException {

    public CollectionAccessDeniedException() {
        super("접근 권한이 없습니다.");
    }
}
