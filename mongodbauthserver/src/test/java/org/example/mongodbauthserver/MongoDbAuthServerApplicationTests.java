package org.example.mongodbauthserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value={"test"})
@SpringBootTest
class MongoDbAuthServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
