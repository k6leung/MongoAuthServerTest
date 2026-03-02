package org.example.mongodbauthserver.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.example.mongodbauthserver.converter.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class MongoOAuth2SpecialMappingConfiguration extends AbstractMongoClientConfiguration {

    @Value("${mongodb.authdb}")
    private String databaseName;

    @Value("${mongo.connection.string}")
    private String mongoConnectionString;

    @Autowired
    private OidcIdTokenConverter oidcIdTokenConverter;

    @Autowired
    private JwtConverter jwtConverter;

    //@Autowired
    //private OAuth2AuthorizationCodeConverter oAuth2AuthorizationCodeConverter;

    @Override
    protected String getDatabaseName() {
        return this.databaseName;
    }

    @Override
    public MongoClient mongoClient() {
        log.info("mongo connection string: {}", this.mongoConnectionString);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoConnectionString))
                .build();

        return MongoClients.create(settings);
    }

    @Override
    protected void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter adapter) {
        adapter.registerConverter(new OAuth2AuthorizationCodeConverter());
        adapter.registerConverter(new OAuth2RefreshTokenConverter());
        adapter.registerConverter(new OAuth2AccessTokenConverter());
        adapter.registerConverter(this.oidcIdTokenConverter);
        adapter.registerConverter(this.jwtConverter);
        adapter.registerConverter(new OAuth2DeviceCodeConverter());
        adapter.registerConverter(new OAuth2UserCodeConverter());
    }

    // for json serialization
    @Bean
    @Override
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory databaseFactory, MongoCustomConversions customConversions, MongoMappingContext mappingContext) {
        MappingMongoConverter converter = super.mappingMongoConverter(databaseFactory, customConversions, mappingContext    );
        converter.preserveMapKeys(true);
        return converter;
    }

    /*@Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.readPreference(ReadPreference.secondaryPreferred());
    }*/

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
