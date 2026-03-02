package org.example.resourceserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(TestDataHint.class)
public class NativeConfig {
}
