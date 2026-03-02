package org.example.mongodbauthserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "MongoDBUser")
public class MongoDBUser {

    @Id
    private ObjectId id;

    @Indexed
    private String username;

    private String password;

    private Boolean enabled;

    @DocumentReference
    private List<MongoDBRole> roleList;
}
