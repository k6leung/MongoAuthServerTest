package org.example.mongodbauthserver.repository;

import org.example.mongodbauthserver.model.MongoDBRegisteredClient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoDBRegisteredClientRepository extends MongoRepository<MongoDBRegisteredClient, String> {

    Optional<MongoDBRegisteredClient> findByClientId(String clientId);

}
