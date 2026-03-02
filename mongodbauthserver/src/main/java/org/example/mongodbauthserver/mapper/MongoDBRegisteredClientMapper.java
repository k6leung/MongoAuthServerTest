package org.example.mongodbauthserver.mapper;

import org.apache.commons.lang3.StringUtils;
import org.example.mongodbauthserver.model.MongoDBRegisteredClient;
import org.mapstruct.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.ConfigurationSettingNames;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MongoDBRegisteredClientMapper {


    default RegisteredClient mapRegisteredClient(MongoDBRegisteredClient mongoDBRegisteredClient, ObjectMapper objectMapper) {
        if(mongoDBRegisteredClient != null) {
            Map<String, Object> tokenSettings = this.mapTokenSettings(mongoDBRegisteredClient.getTokenSettingJson(), objectMapper);
            TokenSettings.Builder tokenSettingsBuilder = TokenSettings.withSettings(tokenSettings);

            if(!tokenSettings.containsKey(ConfigurationSettingNames.Token.ACCESS_TOKEN_FORMAT)) {
                tokenSettingsBuilder.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED);
            }

            RegisteredClient.Builder builder =  RegisteredClient
                    .withId(mongoDBRegisteredClient.getClientId())
                    .clientId(mongoDBRegisteredClient.getClientId())
                    .clientIdIssuedAt(mongoDBRegisteredClient.getClientIdIssuedAt())
                    .clientSecret(mongoDBRegisteredClient.getClientSecret())
                    .clientName(mongoDBRegisteredClient.getClientName())
                    .clientAuthenticationMethods(
                            (authenticationMethods) ->
                                    mongoDBRegisteredClient.getClientAuthenticationMethods().forEach(
                                            authenticationMethod -> authenticationMethods.add(this.mapClientAuthenticationMethod(authenticationMethod))
                                    ))
                    .authorizationGrantTypes(
                            (grantTypes) ->
                                    mongoDBRegisteredClient.getAuthorizationGrantTypes().forEach(
                                            grantType -> grantTypes.add(this.mapAuthorizationGrantType(grantType))
                                    ))
                    .redirectUris(
                            (uris) -> uris.addAll(mongoDBRegisteredClient.getRedirectUris()))
                    .postLogoutRedirectUris(
                            (uris) -> uris.addAll(mongoDBRegisteredClient.getPostLogoutRedirectUris()))
                    .scopes((scopes) -> scopes.addAll(mongoDBRegisteredClient.getScopes()))
                    .clientSettings(ClientSettings.withSettings(
                            this.mapClientSettings(mongoDBRegisteredClient.getClientSettingJson(), objectMapper)
                    ).build())
                    .tokenSettings(tokenSettingsBuilder.build());

            return builder.build();
        }

        return null;
    }

    default ClientAuthenticationMethod mapClientAuthenticationMethod(String clientAuthenticationMethodString) {
        return new ClientAuthenticationMethod(clientAuthenticationMethodString);
    }

    default AuthorizationGrantType mapAuthorizationGrantType(String authorizationGrantTypeString) {
        return new AuthorizationGrantType(authorizationGrantTypeString);
    }

    default Map<String, Object> mapClientSettings(String clientSettingJson, @Context ObjectMapper objectMapper) throws JacksonException {
        //return StringUtils.isNotBlank(clientSettingJson) ? (Map<String, Object>)objectMapper.readValue(clientSettingJson, Object.class) : null;
        return StringUtils.isNotBlank(clientSettingJson) ? objectMapper.readValue(clientSettingJson, new TypeReference<>() {}) : null;
    }

    default Map<String, Object> mapTokenSettings(String tokenSettingJson, @Context ObjectMapper objectMapper) {
        //return StringUtils.isNotBlank(tokenSettingJson) ? (Map<String, Object>)objectMapper.readValue(tokenSettingJson, Object.class) : null;
        return StringUtils.isNotBlank(tokenSettingJson) ? objectMapper.readValue(tokenSettingJson, new TypeReference<>() {}) : null;
    }

    @Mapping(target = "clientAuthenticationMethods", source = "clientAuthenticationMethods", qualifiedByName = "mapClientAuthenticationMethodStringList")
    @Mapping(target = "authorizationGrantTypes", source = "authorizationGrantTypes", qualifiedByName = "mapAuthorizationGrantTypeStringList")
    @Mapping(target = "clientSettingJson", source = "clientSettings", qualifiedByName = "mapClientSettingJson")
    @Mapping(target = "tokenSettingJson", source = "tokenSettings", qualifiedByName = "mapTokenSettingJson")
    MongoDBRegisteredClient mapMongoDBRegisteredClient(RegisteredClient registeredClient, @Context ObjectMapper objectMapper);

    @Named("mapClientAuthenticationMethodStringList")
    default Set<String> mapClientAuthenticationMethodStringList(Set<ClientAuthenticationMethod> methodSet) {
        if(methodSet != null) {
            return methodSet.stream()
                    .filter(Objects::nonNull)
                    .map(this::mapClientAuthenticationMethodString)
                    .collect(Collectors.toSet());
        }

        return null;
    }

    default String mapClientAuthenticationMethodString(ClientAuthenticationMethod method) {
        return method.getValue();
    }

    @Named("mapAuthorizationGrantTypeStringList")
    default Set<String> mapAuthorizationGrantTypeStringList(Set<AuthorizationGrantType> authorizationGrantTypeSet) {
        if(authorizationGrantTypeSet != null) {
            return authorizationGrantTypeSet.stream()
                    .filter(Objects::nonNull)
                    .map(this::mapAuthorizationGrantTypeString)
                    .collect(Collectors.toSet());
        }

        return null;
    }

    default String mapAuthorizationGrantTypeString(AuthorizationGrantType authorizationGrantType) {
        return (authorizationGrantType != null) ? authorizationGrantType.getValue() : null;
    }

    @Named("mapClientSettingJson")
    default String mapClientSettingJson(ClientSettings clientSettings, @Context ObjectMapper objectMapper) {
        return (clientSettings != null) ? objectMapper.writeValueAsString(clientSettings.getSettings()) : null;
    }

    @Named("mapTokenSettingJson")
    default String mapTokenSettingJson(TokenSettings tokenSettings, @Context ObjectMapper objectMapper) {
        return (tokenSettings != null) ? objectMapper.writeValueAsString(tokenSettings.getSettings()) : null;
    }
}

