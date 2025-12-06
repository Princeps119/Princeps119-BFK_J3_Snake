package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import data.TokenData;
import exceptions.UserNotFoundException;
import org.bson.Document;
import repository.MongoRepo;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static services.Util.hashPassword;

public class LoginService {

    private static final Logger logger = Logger.getLogger(LoginService.class.getName());
    private static final String USER_COLLECTION_NAME = "users";
    private static final MongoRepo userDB = MongoRepo.getInstance();

    private static LoginService instance;
    private final MongoCollection<Document> userCollection;

    public static synchronized LoginService getInstance() {
        if (instance == null) {
            instance = new LoginService();
        }
        return instance;
    }

    private LoginService() {
            this.userCollection = userDB.getUserCollection(USER_COLLECTION_NAME);
    }


    public TokenData checkLoginData(final String mail, final String password) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        logger.log(Level.INFO, "in checkLoginData");
        Document foundDocument = userCollection.find(Filters.eq("mail", mail)).first();

        if (foundDocument != null) {
            final Optional<String> optHashedPassword = Optional.ofNullable(foundDocument.get("hashedPassword").toString());
            final Optional<String> optDbMail = Optional.ofNullable(foundDocument.get("mail").toString());

            if (optHashedPassword.isPresent() && optDbMail.isPresent()) {
                if (optHashedPassword.get().equals(hashPassword(password)) && mail.equals(optDbMail.get())) {
                    Optional<String> optUsername = Optional.ofNullable(foundDocument.get("username").toString());

                    try {
                        // todo create token with uid and mail and timestamp to be saved in different collection
                        final String loadedUsername = optUsername.orElse(null);
                        final Instant timestamp = Instant.now();
                        final String userMail = optDbMail.get();

                        return new TokenData(loadedUsername, services.TokenEncrypter.encrypt(userMail), timestamp.toString());
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error while encrypting username and password", e);
                        return null;
                    }
                }
            }
        }
        logger.log(Level.WARNING, "user not found");
        throw new UserNotFoundException("no document found");
    }
}
