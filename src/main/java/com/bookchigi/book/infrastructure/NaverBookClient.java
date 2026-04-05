package com.bookchigi.book.infrastructure;

import com.bookchigi.book.dto.NaverBookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NaverBookClient {

    private final RestClient restClient;

    public NaverBookClient(
            @Value("${naver.api.client-id}") String clientId,
            @Value("${naver.api.client-secret}") String clientSecret
    ) {
        this.restClient = RestClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }

    public NaverBookResponse search(String query, int page, int size) {
        int start = (page * size) + 1;

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/book.json")
                        .queryParam("query", query)
                        .queryParam("display", size)
                        .queryParam("start", start)
                        .build())
                .retrieve()
                .body(NaverBookResponse.class);
    }
}
