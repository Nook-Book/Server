package com.nookbook.domain.book.applicaiton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookbook.domain.book.dto.response.BestSellerRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BestSellerCacheService {

    private final CacheManager cacheManager;
    private final AladinService aladinService;

    private static final List<Integer> CATEGORIES = List.of(0, 1, 170, 336, 50940, 55889, 656, 55890, 2913);
    private static final String CACHE_NAME = "bestSellers";

    @EventListener(ApplicationReadyEvent.class)
    public void loadInitialCache() {
        retryableRefreshAll();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledRefresh() {
        log.info("🔁 [Scheduler] refreshAll() 실행됨 - {}", System.currentTimeMillis());
        retryableRefreshAll();
    }

    @Retryable(
            value = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000)
    )
    public void retryableRefreshAll() {
        refreshAllCategoriesAndPages(10, CATEGORIES, 100);
    }

    @Cacheable(value = CACHE_NAME, key = "#category + '-' + #page")
    public BestSellerRes getBestSellerFromCache(int page, int category, int size) {
        return getBestSellerFromApi(page, category, size);
    }

    public BestSellerRes getBestSellerFromApi(int page, int category, int size) {
        String json = aladinService.callAladinBestSellers(page, category, size);
        return convertToBestSellerRes(json);
    }

    public void refreshCategoryAndPage(int page, int category, int size) {
        BestSellerRes result = getBestSellerFromApi(page, category, size);
        String cacheKey = category + "-" + page;
        cacheManager.getCache(CACHE_NAME).put(cacheKey, result);
    }

    public void refreshAllCategoriesAndPages(int totalPages, List<Integer> categories, int size) {
        for (int category : categories) {
            for (int page = 1; page <= totalPages; page++) {
                refreshCategoryAndPage(category, page, size);
            }
        }
    }

    private BestSellerRes convertToBestSellerRes(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 BestSellerRes 객체로 변환
            return objectMapper.readValue(json, BestSellerRes.class);
        } catch (Exception e) {
            System.err.println("Error converting JSON to BestSellerRes: " + e.getMessage());
            e.printStackTrace();
            return new BestSellerRes(); // 오류 발생 시 빈 객체 반환
        }
    }
}
