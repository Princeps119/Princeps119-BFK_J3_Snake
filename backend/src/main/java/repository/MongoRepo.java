package repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;

public class MongoRepo {

    private static MongoRepo instance;
    private final MongoClient mongoClient;
    private final MongoDatabase userDB;

    private MongoRepo() {
        final String uri = System.getenv("MONGO_URI"); // von docker compose environment

        if (uri == null || uri.isBlank()) {
            throw new IllegalStateException("MONGO_URI environment variable is not set!");

        } else {
            mongoClient = MongoClients.create(uri);
            userDB = mongoClient.getDatabase("snake_userData");
        }

    }

    public static synchronized MongoRepo getInstance() {
        if (instance == null) {
            instance = new MongoRepo();
        }
        return instance;
    }

    public MongoDatabase getUserDB() {
        boolean exists = userDB.listCollectionNames()
                .into(new ArrayList<>())
                .contains("users");

        //cannot check for null for that collection as is created lazily and will always return an obj for userDB.getCollection("users")
        if (!exists) {
            userDB.createCollection("users");
        }
        return userDB;
    }

    public MongoCollection<Document> getUserCollection(final String collectionName) {
        return getUserDB().getCollection(collectionName);
    }


}
