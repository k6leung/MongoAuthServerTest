package org.example.mongodbauthserver.repository;

import org.example.mongodbauthserver.jackson.FactorGrantedAuthorityJacksonModule;
import org.example.mongodbauthserver.mapper.MongoDBRegisteredClientMapper;
import org.example.mongodbauthserver.model.MongoDBRegisteredClient;
import org.example.mongodbauthserver.service.MongoDBOAuth2AuthorizationService;
import org.jspecify.annotations.Nullable;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson.OAuth2AuthorizationServerJacksonModule;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class SpringSecurityMongoDBRegisteredClientRepositoryImpl implements RegisteredClientRepository {

    private final MongoDBRegisteredClientRepository repository;

    private final MongoDBRegisteredClientMapper recordMapper;

    private final ObjectMapper objectMapper;

    public SpringSecurityMongoDBRegisteredClientRepositoryImpl(MongoDBRegisteredClientRepository repository, MongoDBRegisteredClientMapper recordMapper) {
        super();

        this.repository =  repository;
        this.recordMapper = recordMapper;

        this.objectMapper = Jackson3.createJsonMapper();
    }

    public static ObjectMapper setupObjectMapper() {
        List<JacksonModule> modules = SecurityJacksonModules.getModules(SpringSecurityMongoDBRegisteredClientRepositoryImpl.class.getClassLoader());

        return (JsonMapper.builder()
                .addModules(modules)
                .addModules(new OAuth2AuthorizationServerJacksonModule()))
                .build();
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        MongoDBRegisteredClient mappedClient = this.recordMapper.mapMongoDBRegisteredClient(registeredClient, this.objectMapper);

        this.repository.save(mappedClient);
    }

    @Override
    public @Nullable RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        // note: we use clientId field as id because JdbcRegisteredClientRepository uses clientId instead of actual record id
        Optional<MongoDBRegisteredClient> targetOptional = this.repository.findByClientId(id);

        return targetOptional.map(record ->
                        this.recordMapper.mapRegisteredClient(record, this.objectMapper))
                .orElse(null);
    }

    @Override
    public @Nullable RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");

        return this.repository.findByClientId(clientId)
                .map(record -> this.recordMapper.mapRegisteredClient(record, this.objectMapper))
                .orElse(null);
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
