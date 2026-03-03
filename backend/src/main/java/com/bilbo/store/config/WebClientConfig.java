package com.bilbo.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@Configuration
@SuppressWarnings("deprecation") // Suppress warnings for deprecated classes to allow compilation
public class WebClientConfig {

    @Bean
    public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        var oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2.setDefaultOAuth2AuthorizedClient(true);
        oauth2.setDefaultClientRegistrationId("auth0");
        return WebClient.builder()
                .apply(oauth2.oauth2Configuration())
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${spring.security.oauth2.client.registration.auth0.audience}") String audience) {

        var accessTokenResponseClient = new DefaultClientCredentialsTokenResponseClient();

        // Create a custom converter to add the audience parameter by building the request from scratch
        Converter<OAuth2ClientCredentialsGrantRequest, RequestEntity<?>> requestEntityConverter = grantRequest -> {
            ClientRegistration clientRegistration = grantRequest.getClientRegistration();

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("grant_type", grantRequest.getGrantType().getValue());
            parameters.add("client_id", clientRegistration.getClientId());
            parameters.add("client_secret", clientRegistration.getClientSecret());
            parameters.add("audience", audience);

            URI uri = UriComponentsBuilder.fromUriString(clientRegistration.getProviderDetails().getTokenUri()).build().toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            return new RequestEntity<>(parameters, headers, HttpMethod.POST, uri);
        };

        accessTokenResponseClient.setRequestEntityConverter(requestEntityConverter);

        var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials(configurer -> configurer.accessTokenResponseClient(accessTokenResponseClient))
                .build();

        var authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
