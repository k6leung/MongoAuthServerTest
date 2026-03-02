package org.example.simplewebresource.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SimpleWebResourceConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtIssuerUriConfigurationProperties jwtIssuerUriConfigurationProperties) throws Exception {
        List<String> jwtIssuerUri = jwtIssuerUriConfigurationProperties.getUri();
        log.info("jwtIssuerUri: {}", jwtIssuerUri);
        JwtIssuerAuthenticationManagerResolver authenticationManagerResolver = JwtIssuerAuthenticationManagerResolver
                .fromTrustedIssuers(jwtIssuerUri);

        http.securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth2 ->
                        oauth2
                                .authenticationManagerResolver(authenticationManagerResolver)
                                /*.jwt(Customizer.withDefaults())*/);

        return http.build();
    }

    /*@Bean
    public JwtDecoder clientJwtDecoder(OAuth2ResourceServerProperties properties) {
        return JwtDecoders.fromIssuerLocation(properties.getJwt().getIssuerUri());
    }*/
}
