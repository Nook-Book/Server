package com.nookbook.domain.book.infrastructure;

import com.nookbook.domain.book.application.BestSellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BestSellerScheduler {

    private final BestSellerService cacheService;

    private static final List<Integer> CATEGORIES = List.of(0, 1, 170, 336, 50940, 55889, 656, 55890, 2913);

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
        cacheService.refreshAllCategoriesAndPages(10, CATEGORIES, 100);
    }
}

