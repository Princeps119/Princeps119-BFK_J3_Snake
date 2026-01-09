package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import repository.MongoRepo;

import java.util.logging.Level;
import java.util.logging.Logger;

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

        final String decryptedMail = Util.checkLoginToken(exchange);

        if (decryptedMail == null) {
            return false;
        }

        userCollection.findOneAndDelete(Filters.eq("mail", decryptedMail));
        logger.log(Level.INFO, "Deleted user with mail {0}", decryptedMail);
        return true;
    }
}
