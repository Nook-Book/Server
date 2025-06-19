package com.nookbook.domain.book.application;

import com.nookbook.domain.book.dto.response.BestSellerRes;
import com.nookbook.domain.book.infrastructure.aladin.BestSellerFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BestSellerService {

    private final CacheManager cacheManager;
    private final BestSellerFetcher bestSellerFetcher;

    private static final String CACHE_NAME = "bestSellers";

    @Cacheable(value = CACHE_NAME, key = "#category + '-' + #page")
    public BestSellerRes getBestSellerFromCache(int page, int category, int size) {
        return bestSellerFetcher.fetchBestSeller(page, category, size);
    }

    public void refreshCategoryAndPage(int page, int category, int size) {
        BestSellerRes result = bestSellerFetcher.fetchBestSeller(page, category, size);
        String cacheKey = category + "-" + page;
        cacheManager.getCache(CACHE_NAME).put(cacheKey, result);
    }

    public void refreshAllCategoriesAndPages(int totalPages, List<Integer> categories, int size) {
        for (int category : categories) {
            for (int page = 1; page <= totalPages; page++) {
                refreshCategoryAndPage(page, category, size);
            }
        }
    }

}
