package org.example.mongodbauthserver.mongock.config;

import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.springboot.EnableMongock;
import io.mongock.runner.springboot.MongockSpringboot;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableMongock
public class MongockConfiguration {

    @Bean @Primary
    public SpringDataMongoV4Driver springDataMongoV4Driver(MongoTemplate mongoTemplate) {
        SpringDataMongoV4Driver driver = SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);
        driver.enableTransaction();

        return driver;
    }

    @Bean
    public MongockRunner mockgockRunner(ApplicationContext appCtx, ApplicationEventPublisher eventPublisher, SpringDataMongoV4Driver springDataMongoV4Driver) {
        MongockRunner mongockRunner =  MongockSpringboot.builder()
                .setDriver(springDataMongoV4Driver)
                // this line is added as a remedy to a bug, the actual setting
                // is in the application.yml
                .addMigrationScanPackage("org.example.mongodbtest.mongock.migration")
                .setSpringContext(appCtx)
                .setEventPublisher(eventPublisher)
                .setTrackIgnored(true)
                .setTransactional(true)
                .buildRunner();

        return mongockRunner;
    }
}
