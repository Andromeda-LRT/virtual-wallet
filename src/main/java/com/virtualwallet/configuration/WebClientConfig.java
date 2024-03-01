package com.virtualwallet.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Collections;
import static com.virtualwallet.model_helpers.ModelConstantHelper.DUMMY_API_BASE_URL;

@Configuration
public class WebClientConfig {



    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(DUMMY_API_BASE_URL)
                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", DUMMY_API_BASE_URL))
                .build();
    }

}
