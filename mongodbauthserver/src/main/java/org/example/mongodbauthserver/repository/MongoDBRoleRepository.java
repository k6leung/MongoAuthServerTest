package org.example.mongodbauthserver.repository;

import org.bson.types.ObjectId;
import org.example.mongodbauthserver.model.MongoDBRole;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDBRoleRepository extends MongoRepository<MongoDBRole, ObjectId> {
}
