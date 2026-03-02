package org.example.mongodbauthserver.mongock.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.example.mongodbauthserver.model.MongoDBRegisteredClient;
import org.example.mongodbauthserver.repository.MongoDBRegisteredClientRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@ChangeUnit(id="registeredClientSetupMigration", order="00003", author="ken")
public class RegisteredClientSetupMigration {

    private MongoDBRegisteredClient insertedClient;

    @Execution
    public void execution(MongoDBRegisteredClientRepository repository) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");



        String clientSettingJson = """
                {"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}
                """;

        String tokenSettingsJson = """
                {"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}
                """;

        MongoDBRegisteredClient client = new MongoDBRegisteredClient(
                "app-client",
                LocalDateTime.parse("2025-10-12 16:00:37", formatter)
                        .toInstant(ZoneOffset.ofHours(8)),
                "$2a$10$nKEuOBnSzug2zGuy6x1AfuQmSwsXEOsDPD.2VyNy2XJ37TdFPqqrq",
                null,
                "419d2d8d-a79b-4a23-a175-1be0978efaae",
                Set.of("client_secret_basic"),
                Set.of("refresh_token", "authorization_code"),
                Set.of("http://localhost:8080/login/oauth2/code/app-client"),
                Set.of("http://localhost:8080/logout"),
                Set.of("openid", "profile", "email"),
                clientSettingJson,
                tokenSettingsJson);

        insertedClient = repository.save(client);
    }

    @RollbackExecution
    public void rollback(MongoDBRegisteredClientRepository repository) {
        if(insertedClient != null) {
            repository.delete(insertedClient);
        }
    }
}
