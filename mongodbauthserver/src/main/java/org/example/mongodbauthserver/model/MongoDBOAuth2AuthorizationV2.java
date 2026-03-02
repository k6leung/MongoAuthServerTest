package org.example.mongodbauthserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Document(collection = "MongoDBOAuth2AuthorizationV2")
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes(
        value = {
                @CompoundIndex(name = "find_by_token_index", def = "{'tokenList.token.tokenValue':1}"),
                @CompoundIndex(name = "find_by_state_and_token_index", def = "{'state':1,'tokenList.token.tokenValue':1}")
        }
)
public class MongoDBOAuth2AuthorizationV2 {

    @Id
    private String id;
    // this should've been a document reference to registered client, but lets keep it simple and close
    // to original jdbc version
    private String registeredClientId;
    private String principalName;
    private AuthorizationGrantType authorizationGrantType;
    private Set<String> authorizedScopes;
    private List<EmbeddedToken<? extends OAuth2Token>> tokenList;
    private String state;
    private String attributeJson;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmbeddedToken<T extends OAuth2Token> {

        private T token;
        private Map<String, Object> metaData;
    }
}
