package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.sun.net.httpserver.HttpExchange;
import data.Position;
import data.Settings;
import data.SnakePositionData;
import exceptions.UserNotFoundException;
import org.bson.Document;
import repository.MongoRepo;

import java.util.ArrayList;
import java.util.List;
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

        if (foundDocument.containsKey("snakeposition")) {

            createSnakePositionFromFoundDocument(foundDocument);

            return createSnakePositionFromFoundDocument(foundDocument);
        } else {
            throw new UserNotFoundException("user not found");
        }
    }

    private SnakePositionData createSnakePositionFromFoundDocument(Document foundDocument) {

        Document snakeDocs = (Document) foundDocument.get("snakeposition");
        ArrayList<Document> doc = (ArrayList<Document>) snakeDocs.get("snakeposition");
        final List<Position> snakePositions = toPositions(doc);

        final Document settingsDoc = (Document) snakeDocs.get("settings");

        final Settings settings = new Settings(
                settingsDoc.getString("gamesize"),
                settingsDoc.getString("gamespeed")
        );
        final int hs = snakeDocs.getInteger("highscore");

        final String snakedirection = snakeDocs.getString("snakedirection");
        return new SnakePositionData(
                snakePositions,
                settings,
                hs,
                snakedirection
        );
    }

    public boolean saveSnakePosition(HttpExchange exchange, SnakePositionData snakeData) {

        final String decryptedMail = Util.checkLoginToken(exchange, userCollection);

        if (decryptedMail == null) {
            return false;
        }

        try {
            userCollection.updateOne(
                    Filters.eq("mail", decryptedMail),
                    Updates.set("snakeposition", snakeData)
            );

        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "User not found:  " + e.getMessage());
            throw new UserNotFoundException("User not found");
        }
        return true;
    }

    public List<Position> toPositions(List<Document> docs) {
        List<Position> result = new ArrayList<>(docs.size());
        for (Document d : docs) {
            // Handle possible Number types (Integer, Long, Double) and nulls
            Number nx = d.get("x", Number.class);
            Number ny = d.get("y", Number.class);

            if (nx == null || ny == null) {
                // Decide how you want to handle bad data:
                // skip, default to 0, or throw an exception. Examples:
                // continue; // skip
                throw new IllegalArgumentException("Document missing 'x' or 'y': " + d);
            }

            result.add(new Position(nx.intValue(), ny.intValue()));
        }
        return result;
    }

}
