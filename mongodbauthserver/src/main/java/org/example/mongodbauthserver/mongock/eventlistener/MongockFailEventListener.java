package org.example.mongodbauthserver.mongock.eventlistener;

import io.mongock.runner.spring.base.events.SpringMigrationFailureEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

@Slf4j
public class MongockFailEventListener implements ApplicationListener<SpringMigrationFailureEvent> {

    @Override
    public void onApplicationEvent(SpringMigrationFailureEvent event) {
        log.error("[EVENT LISTENER] - Mongock finished with failures: {}",
                event.getMigrationResult().getException().getMessage());
    }

}