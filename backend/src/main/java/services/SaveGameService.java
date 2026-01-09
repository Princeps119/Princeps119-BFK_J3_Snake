package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.sun.net.httpserver.HttpExchange;
import data.SnakePositionData;
import exceptions.UserNotFoundException;
import org.bson.Document;
import repository.MongoRepo;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SaveGameService {
    private static final Logger logger = Logger.getLogger(SaveGameService.class.getName());
    private static final String USER_COLLECTION_NAME = "users";
    private static final MongoRepo userDB = MongoRepo.getInstance();

    private static SaveGameService instance;
    private final MongoCollection<Document> userCollection;

    public static synchronized SaveGameService getInstance() {
        if (instance == null) {
            instance = new SaveGameService();
        }
        return instance;
    }

    private SaveGameService() {
        this.userCollection = userDB.getUserCollection(USER_COLLECTION_NAME);
    }

    public SnakePositionData loadGame(final HttpExchange exchange) {

        final String decryptedMail = Util.checkLoginToken(exchange, userCollection);

        if (decryptedMail == null) {
            throw new IllegalArgumentException("Invalid login token");
        }

        final Document foundDocument = userCollection.find(Filters.eq("mail", decryptedMail)).first();

        if (foundDocument == null) {
            throw new UserNotFoundException("null document");
        }

        if (foundDocument.containsKey("SnakePositionData")) {

            return (SnakePositionData) foundDocument.get("SnakePositionData");

        } else {
            throw new UserNotFoundException("user not found");
        }
    }

    public boolean saveSnakePosition(HttpExchange exchange, SnakePositionData snakeData) {

        final String decryptedMail = Util.checkLoginToken(exchange, userCollection);

        if (decryptedMail == null) {
            return false;
        }

        try {
            userCollection.updateOne(
                    Filters.eq("mail", decryptedMail),
                    Updates.set("SnakePositionData", snakeData)
            );

        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "User not found:  " + e.getMessage());
            throw new UserNotFoundException("User not found");
        }
        return true;
    }
}
