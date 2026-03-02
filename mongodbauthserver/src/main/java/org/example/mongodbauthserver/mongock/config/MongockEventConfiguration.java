package org.example.mongodbauthserver.mongock.config;

import org.example.mongodbauthserver.mongock.eventlistener.MongockFailEventListener;
import org.example.mongodbauthserver.mongock.eventlistener.MongockStartedEventListener;
import org.example.mongodbauthserver.mongock.eventlistener.MongockSuccessEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongockEventConfiguration {

    @Bean
    public MongockStartedEventListener mongockStartedEventListener() {
        return new MongockStartedEventListener();
    }

    @Bean
    public MongockSuccessEventListener mongockSuccessEventListener() {
        return new MongockSuccessEventListener();
    }

    @Bean
    public MongockFailEventListener mongockFailEventListener() {
        return new MongockFailEventListener();
    }
}
