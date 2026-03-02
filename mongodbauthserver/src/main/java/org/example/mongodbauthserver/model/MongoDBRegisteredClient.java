package org.example.mongodbauthserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Data
@Document(collection = "MongoDBRegisteredClient")
@NoArgsConstructor
@AllArgsConstructor
public class MongoDBRegisteredClient {

    //todo remove id, cannot jdbc registered client repository uses id and clientId interchangabily
    @Id
    private String clientId;
    private Instant clientIdIssuedAt;
    private String clientSecret;
    private Instant clientSecretExpiresAt;
    private String clientName;
    private Set<String> clientAuthenticationMethods;
    private Set<String> authorizationGrantTypes;
    private Set<String> redirectUris;
    private Set<String> postLogoutRedirectUris;
    private Set<String> scopes;
    private String clientSettingJson;
    private String tokenSettingJson;
}
