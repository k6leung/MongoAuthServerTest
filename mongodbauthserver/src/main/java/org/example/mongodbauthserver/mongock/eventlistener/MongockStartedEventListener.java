package org.example.mongodbauthserver.mongock.eventlistener;

import io.mongock.runner.spring.base.events.SpringMigrationStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

@Slf4j
public class MongockStartedEventListener implements ApplicationListener<SpringMigrationStartedEvent> {

    @Override
    public void onApplicationEvent(SpringMigrationStartedEvent event) {
        log.info("[EVENT LISTENER] - Mongock STARTED successfully");
    }

}
