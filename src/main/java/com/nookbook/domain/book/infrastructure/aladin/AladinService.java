package com.nookbook.domain.book.infrastructure.aladin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AladinService {

    private final AladinUriBuilder uriBuilder;
    private final AladinWebClient webClient;

    public String callAladinSearchBooks(String keyword, int page) {
        URI uri = uriBuilder.buildSearchUri(keyword, page);
        return webClient.get(uri);
    }

    public String callAladinBestSellers(int page, int category, int size) {
        URI uri = uriBuilder.buildBestSellerUri(page, category, size);
        return webClient.get(uri);
    }

    public String callAladinBookDetail(String isbn13) {
        URI uri = uriBuilder.buildBookDetailUri(isbn13);
        return webClient.get(uri);
    }
}