package org.example.mongodbauthserver.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.Nullable;

import java.util.HashMap;

public class RedisSessionHint implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        hints.serialization()
                .registerType(HashMap.class);
    }
}