package org.example.mongodbauthserver.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.example.mongodbauthserver.jwt.Oauth2AccessTokenCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
public class DefaultSecurityConfig {

    public final static String APP_CLIENT = "app-client";

    public static final String POSTMAN_CLIENT_ID = "postman-client";

    private final Oauth2AccessTokenCustomizer tokenCustomizer;

    @Bean
    public OAuth2TokenGenerator<OAuth2Token> tokenGenerator(JWKSource<SecurityContext> jwkSource) {
        JwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource);
        JwtGenerator jwtAccessTokenGenerator = new JwtGenerator(jwtEncoder);
        jwtAccessTokenGenerator.setJwtCustomizer(tokenCustomizer);

        OAuth2RefreshTokenGenerator refreshTokenGenerator = new  OAuth2RefreshTokenGenerator();

        return new DelegatingOAuth2TokenGenerator(jwtAccessTokenGenerator, refreshTokenGenerator);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, authorizationServer ->
                        authorizationServer.oidc(Customizer.withDefaults()) // enable openid connect
                )
                .authorizeHttpRequests(
                        (authorize) ->
                                authorize.anyRequest().authenticated());
        // -- ENDS HERE

        http
                .exceptionHandling((exceptions) -> // If any errors occur redirect user to login page
                        exceptions.defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                // enable auth server to accept JWT for endpoints such as /userinfo
                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(Customizer.withDefaults()));

        return http
                .formLogin(
                        formLogin -> formLogin
                                .defaultSuccessUrl("/loginSuccess"))
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .formLogin(formLogin -> formLogin
                        .defaultSuccessUrl("/loginSuccess")) // Enable form login
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/logoutSuccess").permitAll()
                                .anyRequest().authenticated())
                .logout(
                        logout ->
                                logout
                                        .logoutUrl("/logout")
                                        .logoutSuccessUrl("/logoutSuccess")
                                        .invalidateHttpSession(true)
                                        .deleteCookies("JSESSIONID", "SESSION")
                                        .clearAuthentication(true)
                );

        return http.build();
    }
}
