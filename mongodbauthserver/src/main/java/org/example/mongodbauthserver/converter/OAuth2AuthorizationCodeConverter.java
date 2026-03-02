package org.example.mongodbauthserver.converter;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;

import java.time.Instant;
import java.util.Date;

public class OAuth2AuthorizationCodeConverter implements Converter<Document, OAuth2AuthorizationCode> {
    @Override
    public OAuth2AuthorizationCode convert(Document source) {
        String tokenValue = source.getString("tokenValue");
        Date issuedAtDate = source.getDate("issuedAt");
        Instant issuedAt = (issuedAtDate != null) ? issuedAtDate.toInstant() : null;
        Date expiresAtDate = source.getDate("expiresAtDate");
        Instant expiresAt = (expiresAtDate != null) ? issuedAtDate.toInstant() : null;

        return new OAuth2AuthorizationCode(tokenValue, issuedAt, expiresAt);
    }
}
