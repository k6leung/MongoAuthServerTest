package org.example.apigateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationCodeAuthenticationTokenConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationConverterServerWebExchangeMatcher;
import org.springframework.security.web.server.authentication.DelegatingServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ott.ServerOneTimeTokenAuthenticationConverter;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import org.springframework.security.web.server.util.matcher.AndServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
public class SecurityConfig {

    private WebSessionServerRequestCache requestCache(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        ServerWebExchangeMatcher get = ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, new String[]{"/**"});
        ServerWebExchangeMatcher notFavicon = new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers(new String[]{"/favicon.*"}));

        ServerOAuth2AuthorizationCodeAuthenticationTokenConverter authorizationCodeAuthenticationTokenConverter = new ServerOAuth2AuthorizationCodeAuthenticationTokenConverter(clientRegistrationRepository);
        ServerOneTimeTokenAuthenticationConverter oneTimeTokenAuthenticationConverter = new ServerOneTimeTokenAuthenticationConverter();
        DelegatingServerAuthenticationConverter delegatingServerAuthenticationConverter =
                new DelegatingServerAuthenticationConverter(oneTimeTokenAuthenticationConverter, authorizationCodeAuthenticationTokenConverter);
        delegatingServerAuthenticationConverter.setContinueOnError(true);

        AuthenticationConverterServerWebExchangeMatcher authenticationConverterServerWebExchangeMatcher = new AuthenticationConverterServerWebExchangeMatcher(delegatingServerAuthenticationConverter);

        NegatedServerWebExchangeMatcher notAuthenticatedMatcher = new NegatedServerWebExchangeMatcher(authenticationConverterServerWebExchangeMatcher);

        ServerWebExchangeMatcher saveRequestMatcher = new AndServerWebExchangeMatcher(get, notFavicon, notAuthenticatedMatcher);

        // only save get requests that is not favicon and unauthenticated
        WebSessionServerRequestCache requestCache = new WebSessionServerRequestCache();
        requestCache.setSaveRequestMatcher(saveRequestMatcher);

        return requestCache;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            @Autowired ReactiveClientRegistrationRepository clientRegistrationRepository) {
        http.authorizeExchange((exchange) ->
                exchange
                        // added to try keep authorization server behind gateway
                        .pathMatchers(HttpMethod.POST, "/login").permitAll()
                        .pathMatchers("/auth/**").permitAll()
                        // added to try keep authorization server behind gateway ends
                        // simply act as a simply api gateway, let resource server to call authorization server to validate token
                        .pathMatchers("/proxyBooks/**").permitAll()
                        .anyExchange().authenticated());
        // added to try keep authorization server behind gateway
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        // added to try keep authorization server behind gateway ends
        http.oauth2Login(Customizer.withDefaults());
        http.oauth2Client(Customizer.withDefaults());
        http.requestCache(
                requestCacheSpec ->
                        requestCacheSpec.requestCache(requestCache(clientRegistrationRepository)));

        return http.build();
    }
}
