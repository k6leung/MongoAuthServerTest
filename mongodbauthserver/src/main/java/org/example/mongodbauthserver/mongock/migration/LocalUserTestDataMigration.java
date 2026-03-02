package org.example.mongodbauthserver.mongock.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.example.mongodbauthserver.model.MongoDBRole;
import org.example.mongodbauthserver.model.MongoDBUser;
import org.example.mongodbauthserver.repository.MongoDBRoleRepository;
import org.example.mongodbauthserver.repository.MongoDBUserRepository;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile("test")
@ChangeUnit(id="localUserTestDataMigration", order="00002", author="ken")
public class LocalUserTestDataMigration {

    private MongoDBUser testUserRecord;

    @Execution
    public void execute(MongoDBUserRepository userRepository, MongoDBRoleRepository roleRepository) {
        List<MongoDBRole> allRoleList = roleRepository.findAll();

        MongoDBUser testUser = new MongoDBUser(
            null,
                "ken",
                "$2a$10$VFzHI922gLQV2Is/JDDw8eIdrQtAc8Y9Ft1Nv6vO15VxIyWrs9rC2",
                true,
                allRoleList
        );

        testUserRecord = userRepository.save(testUser);
    }

    @RollbackExecution
    public void rollback(MongoDBUserRepository userRepository) {
        if(testUserRecord != null) {
            userRepository.delete(testUserRecord);
        }
    }
}
