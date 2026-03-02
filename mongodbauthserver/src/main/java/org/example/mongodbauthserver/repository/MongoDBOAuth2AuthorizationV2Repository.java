package org.example.mongodbauthserver.repository;

import org.example.mongodbauthserver.model.MongoDBOAuth2AuthorizationV2;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoDBOAuth2AuthorizationV2Repository extends MongoRepository<MongoDBOAuth2AuthorizationV2, String> {

    //@Query("{'tokenList.token.tokenValue':  ?0}")
    Optional<MongoDBOAuth2AuthorizationV2> findFirstByTokenList_token_tokenValue(String tokenValue);

    Optional<MongoDBOAuth2AuthorizationV2> findFirstByState(String state);

    /*@Query("""
        {
            $or: [{'tokenList.token.tokenValue': ?0}, {'state': ?0}]
        }
    """)
    Optional<MongoDBOAuth2AuthorizationV2> findFirstByTokenValueWithTokenTypeNull(String tokenValue);*/

    Optional<MongoDBOAuth2AuthorizationV2> findFirstByStateOrTokenList_token_tokenValue(String state, String tokenValue);
}
