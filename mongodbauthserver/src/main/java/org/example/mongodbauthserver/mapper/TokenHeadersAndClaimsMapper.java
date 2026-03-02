package org.example.mongodbauthserver.mapper;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TokenHeadersAndClaimsMapper {

    public Map<String, Object> convertMapDateValueToInstant(Map<String, Object> map) {
        if(map != null) {
            Map<String, Object> result = new HashMap<>(map);

            result.entrySet().stream()
                    .peek(entry -> {
                        if(entry.getValue() instanceof Date dateValue) {
                            entry.setValue(dateValue.toInstant());
                        }
                    })
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
            return result;
        }

        return null;
    }
}
