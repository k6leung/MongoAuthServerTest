package org.example.mongodbauthserver.repository;

import org.bson.types.ObjectId;
import org.example.mongodbauthserver.model.MongoDBAuthority;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDBAuthorityRepository extends MongoRepository<MongoDBAuthority, ObjectId> {

}
