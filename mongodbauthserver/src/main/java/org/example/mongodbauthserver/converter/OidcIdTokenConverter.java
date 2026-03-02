package org.example.mongodbauthserver.converter;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.example.mongodbauthserver.mapper.TokenHeadersAndClaimsMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OidcIdTokenConverter implements Converter<Document, OidcIdToken> {

    private final TokenHeadersAndClaimsMapper tokenHeadersAndClaimsMapper;

    @Override
    public OidcIdToken convert(Document source) {
        String tokenValue = source.getString("tokenValue");
        Date issuedAtDate = source.getDate("issuedAt");
        Instant issuedAt = (issuedAtDate != null) ? issuedAtDate.toInstant() : null;
        Date expiresAtDate = source.getDate("expiresAtDate");
        Instant expiresAt = (expiresAtDate != null) ? issuedAtDate.toInstant() : null;

        Map<String, Object> claims =
                tokenHeadersAndClaimsMapper.convertMapDateValueToInstant(source.get("claims", Document.class));

        return new OidcIdToken(tokenValue, issuedAt, expiresAt, claims);
    }
}
