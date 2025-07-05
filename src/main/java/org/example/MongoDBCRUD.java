package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents CRUD operations in
 * MongoDB, has constructor with main parameters
 */
public class MongoDBCRUD {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public MongoDBCRUD(String connectionString, String dbName, String collectionName) {
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(dbName);
        this.collection = database.getCollection(collectionName);
    }

    /**
     * Close current connection
     */
    public void close() {
        mongoClient.close();
        System.out.println("Connection with MongoDB has been closed.");
    }

    /**
     * Create collection
     */
    public void createCollection(){
        database.createCollection(collection.getNamespace().getCollectionName());
        System.out.println("Collection has been created");
    }

    /**
     * Create document
     * @param document document that will be created
     */
    public void insertDocument(Document document) {
        collection.insertOne(document);
        System.out.println("Document added: " + document);
    }

    /**
     * Create documents
     * @param documents list of documents
     */
    public void insertDocuments(List<Document> documents){
        collection.insertMany(documents);
        System.out.println("Documents added: " + documents.size());
    }

    /**
     * Get list of documents
     * @return array list of documents
     */
    public List<Document> getAllDocuments() {
        return collection.find().into(new ArrayList<>());
    }

    /**
     * Get document by id
     * @param fieldName name of specified field
     * @param value field value
     * @return document with defined parameters
     */
    public Document getDocumentById(String fieldName, Object value) {
        Bson filter = Filters.eq(fieldName, value);
        return collection.find(filter).first();
    }

    /**
     * Update document
     * @param fieldName name of specified field
     * @param fieldValue field value
     * @param updateField new field value
     * @param newValue new value of updated field
     */
    public void updateDocument(String fieldName, Object fieldValue, String updateField, Object newValue) {
        Bson filter = Filters.eq(fieldName, fieldValue);
        Bson update = Updates.set(updateField, newValue);
        collection.updateOne(filter, update);
        System.out.println("Document has been updated (field '" + updateField + "')");
    }

    /**
     * Delete document
     * @param fieldName name of specified field
     * @param value field value
     */
    public void deleteDocument(String fieldName, Object value) {
        Bson filter = Filters.eq(fieldName, value);
        collection.deleteOne(filter);
        System.out.println("Document has been deleted (by field '" + fieldName + "')");
    }

    /**
     * Delete documents
     * @param fieldName name of specified field
     * @param value field value
     */
    public void deleteDocuments(String fieldName, Object value) {
        Bson filter = Filters.eq(fieldName, value);
        long count = collection.deleteMany(filter).getDeletedCount();
        System.out.println(count + " documents deleted (by field '" + fieldName + "')");
    }
}
