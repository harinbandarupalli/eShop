package com.bilbo.store.client;

import com.bilbo.store.client.dto.Auth0User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class Auth0Client {

    private final WebClient webClient;
    private final String auth0ApiBaseUrl;

    public Auth0Client(@Qualifier("webClient") WebClient webClient,
                       @Value("${auth0.api.base-url}") String auth0ApiBaseUrl) {
        this.webClient = webClient;
        this.auth0ApiBaseUrl = auth0ApiBaseUrl;
    }

    public Mono<Auth0User> getUser(String sub) {
        String userUrl = auth0ApiBaseUrl + "users/{sub}";
        return this.webClient.get()
                .uri(userUrl, sub)
                .retrieve()
                .bodyToMono(Auth0User.class);
    }
}
