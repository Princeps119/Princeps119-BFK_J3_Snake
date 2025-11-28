package repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import services.LoginService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoRepo {

    private static MongoRepo instance;
    private final MongoClient mongoClient;
    private final MongoDatabase userDB;

    private MongoRepo() {
         String uri = System.getenv("MONGO_URI"); // von docker compose environment
        if (uri == null) {
            uri = "mongodb://snake_backend:snakesLikeToHisss222@localhost:27018/snake_userData?authSource=admin";
        }

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

            createExampleUser();
        }
        if (userDB.getCollection("users").countDocuments() == 0) {
            createExampleUser();
        }
        return userDB;
    }

    private void createExampleUser() {

        try {
            String username = "exampleUser";
            String email = "example@mail.com";
            String password = "mySecretPassword";
            MongoCollection<Document> users = userDB.getCollection("users");

            // Hash password (SHA-256)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            String hashedPassword = Base64.getEncoder().encodeToString(hash);

            // Create document
            Document userDoc = new Document("username", username)
                    .append("mail", email)
                    .append("hashedPassword", hashedPassword);

            // Insert into collection
            users.insertOne(userDoc);
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(MongoRepo.class.getName()).log(Level.SEVERE, "could not create example user", e);
        }
    }

    public MongoCollection<Document> getUserCollection(final String collectionName) {
        return getUserDB().getCollection(collectionName);
    }
}
