package org.example.mongodbauthserver.repository;

import org.bson.types.ObjectId;
import org.example.mongodbauthserver.model.MongoDBUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoDBUserRepository extends MongoRepository<MongoDBUser, ObjectId>  {

    Optional<MongoDBUser> findByUsername(String username);
}
