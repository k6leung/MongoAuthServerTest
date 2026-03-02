package org.example.mongodbauthserver.mapper;

import org.apache.commons.lang3.StringUtils;
import org.example.mongodbauthserver.model.MongoDBOAuth2AuthorizationConsent;
import org.mapstruct.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MongoDBOAuth2AuthorizationConsentMapper {


    default OAuth2AuthorizationConsent mapOAuth2AuthorizationConsent(MongoDBOAuth2AuthorizationConsent consent) {
        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(consent.getRegisteredClientId(), consent.getPrincipalName());

        consent.getAuthorities().stream()
                .filter(StringUtils::isNotBlank)
                .map(this::mapGrantedAuthority)
                .forEach(builder::authority);

        return builder.build();
    }

    default GrantedAuthority mapGrantedAuthority(String authority) {
        return StringUtils.isNotBlank(authority) ? new SimpleGrantedAuthority(authority) : null;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", source = "authorities", qualifiedByName = "mapAuthoritySet")
    MongoDBOAuth2AuthorizationConsent mapMongoDBOAuth2AuthorizationConsent(OAuth2AuthorizationConsent consent);

    @Named("mapAuthoritySet")
    default Set<String> mapAuthoritySet(Set<GrantedAuthority> authoritySet) {
        if(authoritySet == null) {
            return null;
        }

        return authoritySet.stream()
                .filter(Objects::nonNull)
                .map(this::mapAuthorityString)
                .collect(Collectors.toSet());
    }

    default String mapAuthorityString(GrantedAuthority authority) {
        return (authority != null) ? authority.getAuthority() : null;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", source = "authorities", qualifiedByName = "mapAuthoritySet")
    void mapForUpdate(OAuth2AuthorizationConsent consent, @MappingTarget MongoDBOAuth2AuthorizationConsent record);
}
