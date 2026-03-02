package org.example.mongodbauthserver.mapper;

import io.micrometer.common.util.StringUtils;
import org.example.mongodbauthserver.model.MongoDBOAuth2AuthorizationV2;
import org.mapstruct.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MongoDBOAuth2AuthorizationV2Mapper {

    public static final String META_TOKEN_CLAIM_KEY = "metadata.token.claims";

    default void mapTokens(OAuth2Authorization.Builder builder, List<MongoDBOAuth2AuthorizationV2.EmbeddedToken<? extends OAuth2Token>> tokenList, TokenHeadersAndClaimsMapper tokenHeadersAndClaimsMapper) {
        if(tokenList != null) {
            for(MongoDBOAuth2AuthorizationV2.EmbeddedToken<? extends OAuth2Token> embeddedToken : tokenList) {
                OAuth2Token token = embeddedToken.getToken();

                Map<String, Object> meta = embeddedToken.getMetaData();

                // special handling for Date type -> must convert to java.time.Instant
                if(meta != null && meta.containsKey(META_TOKEN_CLAIM_KEY)) {
                    Object claimsObj = meta.get(META_TOKEN_CLAIM_KEY);

                    Map<String, Object> claims = (Map<String, Object>) claimsObj;
                    claims = tokenHeadersAndClaimsMapper.convertMapDateValueToInstant(claims);
                    meta.put(META_TOKEN_CLAIM_KEY, claims);
                }

                if((token != null) && (meta != null)) {
                    builder.token(token, (metadata) -> metadata.putAll(meta));
                }
            }
        }
    }

    default OAuth2Authorization mapOAuth2Authorization(MongoDBOAuth2AuthorizationV2 record, RegisteredClient registeredClient, ObjectMapper objectMapper, TokenHeadersAndClaimsMapper tokenClaimMapper) {
        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);
        builder.id(record.getId())
                .principalName(record.getPrincipalName())
                .authorizationGrantType(record.getAuthorizationGrantType())
                .authorizedScopes(record.getAuthorizedScopes());

        this.mapTokens(builder, record.getTokenList(), tokenClaimMapper);

        String attributesJson = record.getAttributeJson();
        if(StringUtils.isNotBlank(attributesJson)) {
            //Map<String, Object> attributes = (Map<String, Object>)objectMapper.readValue(attributesJson, Object.class);
            Map<String, Object> attributes = objectMapper.readValue(attributesJson, new TypeReference<>() {});

            builder.attributes((attrs) -> attrs.putAll(attributes));
        }

        String state = record.getState();
        if(StringUtils.isNotBlank(state)) {
            builder.attribute("state", state);
        }

        return builder.build();
    }


    @Mapping(target = "tokenList", source = "authorization", qualifiedByName = "mapTokenList")
    @Mapping(target = "state", source = "attributes", qualifiedByName = "mapStateToMongoDB")
    @Mapping(target = "attributeJson", source = "attributes", qualifiedByName = "mapAttributesToAttributeJson")
    MongoDBOAuth2AuthorizationV2 mapMongoDBOAuth2AuthorizationV2(OAuth2Authorization authorization, @Context ObjectMapper objectMapper);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tokenList", source = "authorization", qualifiedByName = "mapTokenList")
    @Mapping(target = "state", source = "attributes", qualifiedByName = "mapStateToMongoDB")
    @Mapping(target = "attributeJson", source = "attributes", qualifiedByName = "mapAttributesToAttributeJson")
    void mapForUpdate(OAuth2Authorization authorization, @MappingTarget MongoDBOAuth2AuthorizationV2 record, @Context ObjectMapper objectMapper);

    @Named("mapTokenList")
    default List<MongoDBOAuth2AuthorizationV2.EmbeddedToken<? extends OAuth2Token>> mapTokenList(OAuth2Authorization authorization) {
        if(authorization == null) {
            return null;
        }

        List<MongoDBOAuth2AuthorizationV2.EmbeddedToken<? extends OAuth2Token>> result = new ArrayList<>(5);
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        if(authorizationCode != null) {
            MongoDBOAuth2AuthorizationV2.EmbeddedToken<OAuth2AuthorizationCode> authorizationCodeEmbeddedToken = new MongoDBOAuth2AuthorizationV2.EmbeddedToken<>(
                    authorizationCode.getToken(), authorizationCode.getMetadata());

            result.add(authorizationCodeEmbeddedToken);
        }

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        if(accessToken != null) {
            MongoDBOAuth2AuthorizationV2.EmbeddedToken<OAuth2AccessToken> oauth2AccessEmbeddedToken = new MongoDBOAuth2AuthorizationV2.EmbeddedToken<>(
                    accessToken.getToken(), accessToken.getMetadata());

            result.add(oauth2AccessEmbeddedToken);
        }

        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        if(oidcIdToken != null) {
            MongoDBOAuth2AuthorizationV2.EmbeddedToken<OidcIdToken> oidcIdTokenEmbeddedToken = new MongoDBOAuth2AuthorizationV2.EmbeddedToken<>(
                    oidcIdToken.getToken(), oidcIdToken.getMetadata());

            result.add(oidcIdTokenEmbeddedToken);
        }

        OAuth2Authorization.Token<OAuth2UserCode> userCode = authorization.getToken(OAuth2UserCode.class);
        if(userCode != null) {
            MongoDBOAuth2AuthorizationV2.EmbeddedToken<OAuth2UserCode> oAuth2UserCodeEmbeddedToken = new MongoDBOAuth2AuthorizationV2.EmbeddedToken<>(
                    userCode.getToken(), userCode.getMetadata());

            result.add(oAuth2UserCodeEmbeddedToken);
        }

        OAuth2Authorization.Token<OAuth2DeviceCode> deviceCode = authorization.getToken(OAuth2DeviceCode.class);
        if(deviceCode != null) {
            MongoDBOAuth2AuthorizationV2.EmbeddedToken<OAuth2DeviceCode> oAuth2UserCodeEmbeddedToken = new MongoDBOAuth2AuthorizationV2.EmbeddedToken<>(
                    deviceCode.getToken(), deviceCode.getMetadata());

            result.add(oAuth2UserCodeEmbeddedToken);
        }

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if(refreshToken != null) {
            MongoDBOAuth2AuthorizationV2.EmbeddedToken<OAuth2RefreshToken> oAuth2RefreshTokenEmbeddedToken = new MongoDBOAuth2AuthorizationV2.EmbeddedToken<>(
                    refreshToken.getToken(), refreshToken.getMetadata());

            result.add(oAuth2RefreshTokenEmbeddedToken);
        }

        return result;
    }

    @Named("mapStateToMongoDB")
    default String mapStateToMongoDB(Map<String, Object> attributes) {
        return (attributes != null) ? (String)attributes.get("state") : null;
    }

    @Named("mapAttributesToAttributeJson")
    default String mapAttributesToAttributeJson(Map<String, Object> attributes, @Context ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(attributes);
    }
}
