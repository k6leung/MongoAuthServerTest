package org.example.mongodbauthserver.service;

import org.example.mongodbauthserver.jackson.FactorGrantedAuthorityJacksonModule;
import org.example.mongodbauthserver.mapper.MongoDBOAuth2AuthorizationV2Mapper;
import org.example.mongodbauthserver.mapper.TokenHeadersAndClaimsMapper;
import org.example.mongodbauthserver.model.MongoDBOAuth2AuthorizationV2;
import org.example.mongodbauthserver.repository.MongoDBOAuth2AuthorizationV2Repository;
import org.example.mongodbauthserver.repository.SpringSecurityMongoDBRegisteredClientRepositoryImpl;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.jackson.OAuth2AuthorizationServerJacksonModule;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Optional;

@Service
public class MongoDBOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final SpringSecurityMongoDBRegisteredClientRepositoryImpl registeredClientRepository;

    private final MongoDBOAuth2AuthorizationV2Repository authorizationV2Repository;

    private final MongoDBOAuth2AuthorizationV2Mapper recordMapper;

    private final ObjectMapper objectMapper;

    private final TokenHeadersAndClaimsMapper tokenHeadersAndClaimsMapper;

    public MongoDBOAuth2AuthorizationService(SpringSecurityMongoDBRegisteredClientRepositoryImpl registeredClientRepository,
                                             MongoDBOAuth2AuthorizationV2Repository authorizationV2Repository,
                                             MongoDBOAuth2AuthorizationV2Mapper recordMapper,
                                             TokenHeadersAndClaimsMapper tokenHeadersAndClaimsMapper) {
        super();

        this.registeredClientRepository = registeredClientRepository;
        this.authorizationV2Repository = authorizationV2Repository;
        this.recordMapper = recordMapper;
        this.tokenHeadersAndClaimsMapper = tokenHeadersAndClaimsMapper;
        this.objectMapper = Jackson3.createJsonMapper();
    }


    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        MongoDBOAuth2AuthorizationV2 record = this.authorizationV2Repository.findById(authorization.getId())
                .orElse(null);

        if(record == null) {
            this.authorizationV2Repository.save(this.recordMapper.mapMongoDBOAuth2AuthorizationV2(authorization, this.objectMapper));
        } else {
            this.recordMapper.mapForUpdate(authorization, record, this.objectMapper);

            this.authorizationV2Repository.save(record);
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");

        Optional<MongoDBOAuth2AuthorizationV2> recordOptional = this.authorizationV2Repository.findById(authorization.getId());
        recordOptional.ifPresent(this.authorizationV2Repository::delete);
    }

    private @Nullable OAuth2Authorization internalMapRecordOptional(Optional<MongoDBOAuth2AuthorizationV2> recordOptional) {
        if(recordOptional.isPresent()) {
            MongoDBOAuth2AuthorizationV2 record = recordOptional.get();
            String registeredClientId = record.getRegisteredClientId();
            RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(registeredClientId);

            if (registeredClient == null) {
                throw new DataRetrievalFailureException("The RegisteredClient with id '" + registeredClientId
                        + "' was not found in the RegisteredClientRepository.");
            }

            return this.recordMapper.mapOAuth2Authorization(record, registeredClient, this.objectMapper, this.tokenHeadersAndClaimsMapper);
        }
        return null;
    }

    @Override
    public @Nullable OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");

        Optional<MongoDBOAuth2AuthorizationV2> recordOptional = this.authorizationV2Repository.findById(id);

        return this.internalMapRecordOptional(recordOptional);
    }

    @Override
    public @Nullable OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");

        Optional<MongoDBOAuth2AuthorizationV2> recordOptional;
        if (tokenType == null) {
            recordOptional = this.authorizationV2Repository.findFirstByStateOrTokenList_token_tokenValue(token, token);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            recordOptional = this.authorizationV2Repository.findFirstByState(token);
        } else {
            // not state, not null, any other type
            recordOptional = this.authorizationV2Repository.findFirstByTokenList_token_tokenValue(token);
        }

        return this.internalMapRecordOptional(recordOptional);
    }

    private static final class Jackson3 {
        public static ObjectMapper createJsonMapper() {
            List<JacksonModule> modules = SecurityJacksonModules.getModules(MongoDBOAuth2AuthorizationService.class.getClassLoader());
            return (JsonMapper.builder()
                    .addModules(modules))
                    // todo remove this line when bug fixed
                    .addModules(new FactorGrantedAuthorityJacksonModule())
                    .addModules(new OAuth2AuthorizationServerJacksonModule())
                    .build();
        }
    }
}
