package db;

import org.bson.Document;
import org.example.MongoDBCRUD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Testcontainers
public class MongoTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:latest")
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "test")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "test");

    private MongoDBCRUD mongoCRUD;

    @BeforeEach
    void setup() {
        String uri = "mongodb://test:test@" + mongo.getHost() + ":" + mongo.getFirstMappedPort();
        mongoCRUD = new MongoDBCRUD(uri, "testdb", "testcollection");
    }

    @Test
    void testCrud(){
        Document doc = new Document("_id", 1).append("name", "Test");
        mongoCRUD.insertDocument(doc);

        Document found = mongoCRUD.getDocumentById("_id", 1);
        assertEquals("Test", found.getString("name"));

        mongoCRUD.updateDocument("_id", 1, "name", "Updated");
        Document updated = mongoCRUD.getDocumentById("_id", 1);
        assertEquals("Updated", updated.getString("name"));

        mongoCRUD.deleteDocument("_id", 1);
        assertNull(mongoCRUD.getDocumentById("_id", 1));
    }
}
