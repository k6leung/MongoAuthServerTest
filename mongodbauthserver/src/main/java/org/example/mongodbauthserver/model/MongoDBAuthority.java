package org.example.mongodbauthserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="MongoDBAuthority")
public class MongoDBAuthority {

    @Id
    private ObjectId id;

    private String authority;
}
