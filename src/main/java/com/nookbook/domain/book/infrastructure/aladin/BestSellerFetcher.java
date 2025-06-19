package com.nookbook.domain.book.infrastructure.aladin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookbook.domain.book.dto.response.BestSellerRes;
import com.nookbook.domain.book.infrastructure.aladin.AladinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BestSellerFetcher {

    private final AladinService aladinService;
    private final ObjectMapper objectMapper;

    public BestSellerRes fetchBestSeller(int page, int category, int size) {
        String json = aladinService.callAladinBestSellers(page, category, size);
        try {
            return objectMapper.readValue(json, BestSellerRes.class);
        } catch (Exception e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
            return new BestSellerRes();
        }
    }
}

