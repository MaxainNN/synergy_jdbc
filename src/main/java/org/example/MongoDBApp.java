package org.example;

import co.elastic.clients.elasticsearch._types.Result;
import org.bson.Document;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Example of app that connect to MongoDB and
 * execute some requests
 */
public class MongoDBApp {

    private static final String MONGO_URI = "mongodb://test_user:test_pass@localhost:27017";
    private static final String MONGO_DB = "library";
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;
    private static final String ES_HOST = "localhost";
    private static final int ES_PORT = 9200;

    public static void main(String[] args) {
        MongoDBCRUD authorsCRUD = new MongoDBCRUD(MONGO_URI, MONGO_DB, "authors",true);
        MongoDBCRUD booksCRUD = new MongoDBCRUD(MONGO_URI, MONGO_DB, "books",true);
        RedisCacheManager redisManager = new RedisCacheManager(REDIS_HOST, REDIS_PORT);
        ElasticSearchManager esManager = new ElasticSearchManager(ES_HOST, ES_PORT);

        try {
            createCollections(authorsCRUD, booksCRUD);
            insertAuthors(authorsCRUD, redisManager);
            insertBooks(booksCRUD, redisManager, esManager);
            searchExample(esManager);
            cacheExample(redisManager);
            insertBookWithInvalidAuthor(booksCRUD);
            deleteBooksByAuthor(booksCRUD, 1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            authorsCRUD.close();
            booksCRUD.close();
            redisManager.close();
            try {
                esManager.close();
            } catch (IOException e) {
                System.err.println("Error closing ElasticSearch: " + e.getMessage());
            }
        }
    }

    /**
     * Create test collections
     * @param authorsCRUD instance for working with the 'authors' collection
     * @param booksCRUD instance for working with the 'books' collection
     */
    private static void createCollections(MongoDBCRUD authorsCRUD, MongoDBCRUD booksCRUD) {
        authorsCRUD.getCollection().drop();
        booksCRUD.getCollection().drop();
        authorsCRUD.createCollection();
        booksCRUD.createCollection();
        System.out.println("Collections 'authors' and 'books' created.");
    }

    /**
     * Insert test data to Authors collections
     * @param authorsCRUD instance of MongoDBCRUD for working with the 'authors' collection
     * @param redisManager instance of RedisCacheManager
     */
    private static void insertAuthors(MongoDBCRUD authorsCRUD, RedisCacheManager redisManager) {
        List<Document> authors  = Arrays.asList(
                new Document("_id", 1).append("name", "Leo Tolstoy"),
                new Document("_id", 2).append("name", "Fyodor Dostoevsky"),
                new Document("_id", 3).append("name", "Alexander Pushkin"),
                new Document("_id", 4).append("name", "Anton Chekhov"),
                new Document("_id", 5).append("name", "Ivan Turgenev")
        );

        authorsCRUD.insertDocuments(authors );

        authors.forEach(author -> {
            String key = "author:" + author.get("_id");
            redisManager.cacheDocument(key, author.toJson(), 3600);
        });

        System.out.println("Authors added and cached.");
    }

    /**
     * Insert test data to Books collections
     * @param booksCRUD instance of MongoDBCRUD for working with the 'books' collection
     * @param redisManager instance of RedisCacheManager
     * @param esManager instance of ElasticSearchManager
     */
    private static void insertBooks(MongoDBCRUD booksCRUD,
                                    RedisCacheManager redisManager,
                                    ElasticSearchManager esManager){
        List<Document> books = Arrays.asList(
                new Document("_id", 1).append("title", "War and Peace").append("author_id", 1),
                new Document("_id", 2).append("title", "Crime and punishment").append("author_id", 2),
                new Document("_id", 3).append("title", "Eugene Onegin").append("author_id", 3),
                new Document("_id", 4).append("title", "The Sea-gull").append("author_id", 4),
                new Document("_id", 5).append("title", "Fathers and Sons").append("author_id", 5)
        );

        booksCRUD.insertDocuments(books);

        books.forEach(book -> {
            String key = "book:" + book.get("_id");
            redisManager.cacheDocument(key, book.toJson(), 3600);

            try {
                Result result = esManager.indexDocument("books", book.get("_id").toString(), book);
                System.out.println("Book indexed with result: " + result);
            } catch (IOException e) {
                System.err.println("Failed to index book in ElasticSearch: " + e.getMessage());
            }
        });

        System.out.println("Books added, cached and indexed.");
    }

    /**
     * Example of searching document in ElasticSearch
     * @param esManager instance of ElasticSearchManager class
     */
    private static void searchExample(ElasticSearchManager esManager){
        try {
            System.out.println("Searching for books with 'war' in title:");
            List<Document> results = esManager.search("books", "title", "war");
            results.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }

    /**
     * Example of getting cached document from Redis
     * @param redisManager instance of RedisCacheManager class
     */
    private static void cacheExample(RedisCacheManager redisManager){
        String cachedAuthor = redisManager.getCachedDocument("author:1");
        System.out.println("Cached author with ID 1: " + cachedAuthor);
    }

    /**
     * Insert Book that has invalid author
     * @param booksCRUD instance of MongoDBCRUD for working with the 'books' collection
     */
    private static void insertBookWithInvalidAuthor(MongoDBCRUD booksCRUD) {
        booksCRUD.insertDocument(
                new Document("_id", 6).append("title", "Does not exist").append("author_id", 999)
        );
        System.out.println("Book with author that does not exist has been added.");
    }

    /**
     * Delete Book that has specified author
     * @param booksCRUD instance of MongoDBCRUD for working with the 'books' collection
     * @param authorId id of author
     */
    private static void deleteBooksByAuthor(MongoDBCRUD booksCRUD, int authorId) {
        booksCRUD.deleteDocuments("author_id",authorId);
        System.out.println("Book with Author id: " + authorId + " has been deleted.");
    }
}
