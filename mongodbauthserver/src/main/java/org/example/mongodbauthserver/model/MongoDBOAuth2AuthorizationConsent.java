package org.example.mongodbauthserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "MongoDBOAuth2AuthorizationConsent")
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(
        name = "registered_client_id_principle_name_uk",
        unique = true,
        def = "{'registeredClientId': 1, 'principalName': 1}"
)
public class MongoDBOAuth2AuthorizationConsent {

    @Id
    private ObjectId id;

    private String registeredClientId;

    private String principalName;

    private Set<String> authorities;
}
