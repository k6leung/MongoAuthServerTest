package org.example.mongodbauthserver.repository;

import org.bson.types.ObjectId;
import org.example.mongodbauthserver.model.MongoDBOAuth2AuthorizationConsent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoDBOAuth2AuthorizationConsentRepository extends MongoRepository<MongoDBOAuth2AuthorizationConsent, ObjectId> {

    Optional<MongoDBOAuth2AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
}
