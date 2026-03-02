package org.example.mongodbauthserver.config;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2DeviceCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2UserCode;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;

import java.time.Instant;

@Slf4j
public class SpringSecurityHint implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        hints.reflection().registerType(FactorGrantedAuthority.class);
        hints.serialization().registerType(FactorGrantedAuthority.class);
        hints.serialization().registerType(Instant.class);
        hints.reflection().registerType(Instant.class, MemberCategory.ACCESS_DECLARED_FIELDS, MemberCategory.ACCESS_PUBLIC_FIELDS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
        try {
            hints.reflection().registerField(Instant.class.getDeclaredField("seconds"));
            hints.reflection().registerField(Instant.class.getDeclaredField("nanos"));
        } catch (NoSuchFieldException nsfe) {
            log.error(nsfe.getMessage(), nsfe);
        }
        hints.reflection().registerType(TypeReference.of("java.time.Ser"));
        hints.serialization().registerType(TypeReference.of("java.time.Ser"));
        //hints.serialization().registerType(TypeReference.of("java.util.Collections$UnmodifiableMap"));

        hints.serialization().registerType(OAuth2AccessToken.class);
        hints.reflection().registerType(OAuth2AccessToken.class);
        hints.serialization().registerType(OAuth2AuthorizationCode.class);
        hints.reflection().registerType(OAuth2AuthorizationCode.class);
        hints.serialization().registerType(Jwt.class);
        hints.reflection().registerType(Jwt.class);
        hints.serialization().registerType(OAuth2DeviceCode.class);
        hints.reflection().registerType(OAuth2DeviceCode.class);
        hints.serialization().registerType(OAuth2RefreshToken.class);
        hints.reflection().registerType(OAuth2RefreshToken.class);
        hints.serialization().registerType(OAuth2UserCode.class);
        hints.reflection().registerType(OAuth2UserCode.class);
        hints.serialization().registerType(OidcIdToken.class);
        hints.reflection().registerType(OidcIdToken.class);
    }
}
