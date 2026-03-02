package org.example.mongodbauthserver.service;

import lombok.RequiredArgsConstructor;
import org.example.mongodbauthserver.mapper.MongoDBOAuth2AuthorizationConsentMapper;
import org.example.mongodbauthserver.model.MongoDBOAuth2AuthorizationConsent;
import org.example.mongodbauthserver.repository.MongoDBOAuth2AuthorizationConsentRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class MongoDBOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final MongoDBOAuth2AuthorizationConsentMapper mapper;

    private final MongoDBOAuth2AuthorizationConsentRepository repository;

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        MongoDBOAuth2AuthorizationConsent record = repository.findByRegisteredClientIdAndPrincipalName(
                authorizationConsent.getRegisteredClientId(),
                        authorizationConsent.getPrincipalName())
                .orElse(null);

        if(record == null) {
            repository.save(mapper.mapMongoDBOAuth2AuthorizationConsent(authorizationConsent));
        } else {
            mapper.mapForUpdate(authorizationConsent, record);
            repository.save(record);
        }
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        MongoDBOAuth2AuthorizationConsent record = repository.findByRegisteredClientIdAndPrincipalName(
                authorizationConsent.getRegisteredClientId(),
                        authorizationConsent.getPrincipalName())
                .orElse(null);

        if(record != null) {
            repository.delete(record);
        }
    }

    @Override
    public @Nullable OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");

        return repository.findByRegisteredClientIdAndPrincipalName(registeredClientId, principalName)
                .map(mapper::mapOAuth2AuthorizationConsent)
                .orElse(null);
    }
}
