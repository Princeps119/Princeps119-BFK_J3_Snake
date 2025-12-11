package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.sun.net.httpserver.HttpExchange;
import data.TokenData;
import org.bson.Document;
import repository.MongoRepo;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import static services.Util.sendErrorResponse;

public class DeletionService {


    private static final Logger logger = Logger.getLogger(DeletionService.class.getName());
    private static final String USER_COLLECTION_NAME = "users";
    private static final MongoRepo userDB = MongoRepo.getInstance();

    private static DeletionService instance;
    private final MongoCollection<Document> userCollection;

    public static synchronized DeletionService getInstance() {
        if (instance == null) {
            instance = new DeletionService();
        }
        return instance;
    }

    private DeletionService() {
        this.userCollection = userDB.getUserCollection(USER_COLLECTION_NAME);
    }

    public boolean deleteUser (final HttpExchange exchange) {

        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(exchange, 401, "Missing or invalid Authorization header");
            return false;
        }

        String tokenJson = authHeader.substring("Bearer ".length()).trim();

        TokenData tokenData;
        try {
            final JsonReader reader = new JsonReader(new StringReader(tokenJson));
            final Gson gson = new GsonBuilder().create();
            final Type tokenType = new TypeToken<TokenData>() {
            }.getType();

            tokenData = gson.fromJson(reader, tokenType);

        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Malformed token");
            return false;
        }

        String decryptedMail;
        try {
            decryptedMail = TokenEncrypter.decrypt(tokenData.encryptedMail());
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid token encryption");
            return false;
        }

        // check timestamp
        try {
            Instant tokenTime = Instant.parse(tokenData.timestamp());
            if (Duration.between(tokenTime, Instant.now()).toHours() > 4) {
                sendErrorResponse(exchange, 401, "Token expired");
                return false;
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid timestamp format");
            return false;
        }

        userCollection.findOneAndDelete(Filters.eq("mail", decryptedMail));
        logger.log(Level.INFO, "Deleted user with mail {0}", decryptedMail);
        return true;
    }

}
