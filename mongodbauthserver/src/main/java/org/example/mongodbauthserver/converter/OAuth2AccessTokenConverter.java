package org.example.mongodbauthserver.converter;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OAuth2AccessTokenConverter implements Converter<Document, OAuth2AccessToken> {
    @Override
    public OAuth2AccessToken convert(Document source) {
        String tokenValue = source.getString("tokenValue");
        Date issuedAtDate = source.getDate("issuedAt");
        Instant issuedAt = (issuedAtDate != null) ? issuedAtDate.toInstant() : null;
        Date expiresAtDate = source.getDate("expiresAtDate");
        Instant expiresAt = (expiresAtDate != null) ? issuedAtDate.toInstant() : null;

        Document tokenTypeDoc = source.get("tokenType", Document.class);
        String tokenTypeStr = (tokenTypeDoc != null) ? tokenTypeDoc.getString("value") : null;

        OAuth2AccessToken.TokenType tokenType = (tokenTypeStr != null) ? new OAuth2AccessToken.TokenType(tokenTypeStr) : null;

        List<String> scopeFromDoc = source.getList("scopes", String.class);
        Set<String> scopes = (scopeFromDoc != null) ? new HashSet<>(scopeFromDoc) : new HashSet<>();

        return new OAuth2AccessToken(tokenType, tokenValue, issuedAt, expiresAt, scopes);
    }
}
