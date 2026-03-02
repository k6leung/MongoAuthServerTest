package org.example.resourceserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${simple.resource.uri}")
    private String simpleResourceBaseUri;

    @Bean
    public RestClient restClient(RestClient.Builder builder, OAuth2AuthorizedClientManager authorizedClientManager) {
        /*OAuth2ClientHttpRequestInterceptor requestInterceptor =
                new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);

        return builder
                .baseUrl(simpleResourceBaseUri)
                .requestInterceptor(requestInterceptor)
                .build();*/
        return builder.clone()
                .baseUrl(simpleResourceBaseUri)
                .requestInterceptor(
                        (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
                            final var auth = SecurityContextHolder.getContext().getAuthentication();
                            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                                request.getHeaders().setBearerAuth(jwtAuth.getToken().getTokenValue());
                            }
                            return execution.execute(request, body);
                        })
                .build();
    }
}
