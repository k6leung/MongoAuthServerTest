package org.example.mongodbauthserver.mongock.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.example.mongodbauthserver.model.MongoDBAuthority;
import org.example.mongodbauthserver.model.MongoDBRole;
import org.example.mongodbauthserver.repository.MongoDBAuthorityRepository;
import org.example.mongodbauthserver.repository.MongoDBRoleRepository;

import java.util.List;

@ChangeUnit(id="roleMigration", order = "00001", author="ken")
public class AuthorityAndRoleMigration {

    private List<MongoDBAuthority> insertedAuthorityList;

    private List<MongoDBRole> insertedDbRoleList;

    @Execution
    public void execution(MongoDBAuthorityRepository authorityRepository, MongoDBRoleRepository roleRepository) {
        List<MongoDBAuthority> authorityRecords = List.of(
                new MongoDBAuthority(null, "data:create"),
                new MongoDBAuthority(null, "data:update"),
                new MongoDBAuthority(null, "data:delete"),
                new MongoDBAuthority(null, "data:read"));

        insertedAuthorityList = authorityRepository.insert(authorityRecords);

        List<MongoDBAuthority> userAuthorityList = insertedAuthorityList.stream()
                .filter(authority -> "data:read".equals(authority.getAuthority()))
                .toList();
        //throw new RuntimeException("test error");


        List<MongoDBRole> roleRecords = List.of(
                new MongoDBRole(null, "ADMIN", insertedAuthorityList),
                new MongoDBRole(null, "USER", userAuthorityList));

        insertedDbRoleList = roleRepository.insert(roleRecords);
    }

    @RollbackExecution
    public void rollback(MongoDBAuthorityRepository authorityRepository, MongoDBRoleRepository roleRepository) {
        if(insertedDbRoleList != null && !insertedDbRoleList.isEmpty()) {
            roleRepository.deleteAll(insertedDbRoleList);
        }

        if(insertedAuthorityList != null && !insertedAuthorityList.isEmpty()) {
            authorityRepository.deleteAll(insertedAuthorityList);
        }
    }
}
