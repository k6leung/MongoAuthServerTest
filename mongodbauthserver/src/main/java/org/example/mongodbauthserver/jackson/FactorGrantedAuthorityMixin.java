package org.example.mongodbauthserver.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;

// todo: to be removed
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS
)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public abstract class FactorGrantedAuthorityMixin {
    @JsonCreator
    public FactorGrantedAuthorityMixin(@JsonProperty("authority") String role, @JsonProperty("issuedAt") Instant issuedAt) {

    }
}
