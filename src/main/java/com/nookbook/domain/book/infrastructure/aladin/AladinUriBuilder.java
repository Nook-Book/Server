package com.nookbook.domain.book.infrastructure.aladin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class AladinUriBuilder {

    @Value("${aladin.ttb.key}")
    private String key;

    @Value("${aladin.search-url}")
    private String searchUrl;

    @Value("${aladin.list-url}")
    private String listUrl;

    @Value("${aladin.find-url}")
    private String findUrl;

    public URI buildSearchUri(String keyword, int page) {
        return UriComponentsBuilder.fromUriString(searchUrl)
                .queryParam("ttbkey", key)
                .queryParam("Query", keyword)
                .queryParam("QueryType", "Keyword")
                .queryParam("QueryType", "Publisher")
                .queryParam("start", page)
                .queryParam("MaxResults", 10)
                .queryParam("Cover", "Big")
                .queryParam("output", "JS")
                .queryParam("Version", 20131101)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
    }

    public URI buildBestSellerUri(int page, int category, int size) {
        return UriComponentsBuilder.fromUriString(listUrl)
                .queryParam("ttbkey", key)
                .queryParam("QueryType", "Bestseller")
                .queryParam("SearchTarget", "Book")
                .queryParam("start", page)
                .queryParam("MaxResults", size)
                .queryParam("Cover", "Big")
                .queryParam("CategoryId", category)
                .queryParam("output", "JS")
                .queryParam("Version", 20131101)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
    }

    public URI buildBookDetailUri(String isbn13) {
        return UriComponentsBuilder.fromUriString(findUrl)
                .queryParam("ttbkey", key)
                .queryParam("ItemId", isbn13)
                .queryParam("itemIdType", "ISBN13")
                .queryParam("Cover", "Big")
                .queryParam("output", "JS")
                .queryParam("Version", 20131101)
                .queryParam("OptResult", "Toc,fulldescription")
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
    }
}

