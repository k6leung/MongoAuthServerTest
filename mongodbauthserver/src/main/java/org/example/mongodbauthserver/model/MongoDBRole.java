package org.example.mongodbauthserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@Document(collection="MongoDBRole")
@AllArgsConstructor
@NoArgsConstructor
public class MongoDBRole {

    @Id
    private ObjectId id;

    private String role;

    @DocumentReference
    private List<MongoDBAuthority> authorityList;
}
