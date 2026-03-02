package org.example.mongodbauthserver.mongock.eventlistener;

import io.mongock.runner.spring.base.events.SpringMigrationSuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

@Slf4j
public class MongockSuccessEventListener implements ApplicationListener<SpringMigrationSuccessEvent> {

    @Override
    public void onApplicationEvent(SpringMigrationSuccessEvent event) {
        log.info("[EVENT LISTENER] - Mongock finished successfully");
    }

}