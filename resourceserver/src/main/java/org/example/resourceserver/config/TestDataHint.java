package org.example.resourceserver.config;

import org.example.resourceserver.model.TestData;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.Nullable;

public class TestDataHint implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        hints.serialization()
                .registerType(TestData.class);
    }
}
