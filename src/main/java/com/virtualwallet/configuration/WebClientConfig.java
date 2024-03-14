package com.virtualwallet.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient dummyApiWebClient() {
        //todo if all external APIs are consumed without requiring uncommenting below code to safely delete it - TEAM
//        return WebClient.builder()
//                .baseUrl(DUMMY_API_BASE_URL)
//                .defaultCookie("cookieKey", "cookieValue")
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .defaultUriVariables(Collections.singletonMap("url", DUMMY_API_BASE_URL))
//                .build();
        return WebClient.create();
    }

}
