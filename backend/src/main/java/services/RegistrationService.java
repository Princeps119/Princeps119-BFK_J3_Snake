package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import data.RegisterData;
import org.bson.Document;
import repository.MongoRepo;

import java.util.Optional;
import java.util.logging.Logger;

import static services.Util.hashPassword;

public class RegistrationService {

    public static final Logger logger = Logger.getLogger(RegistrationService.class.getName());

    private static RegistrationService instance;
    private final MongoCollection<Document> userCollection;

    public static synchronized RegistrationService getInstance() {
        if (instance == null) {
            instance = new RegistrationService();
        }
        return instance;
    }

    private RegistrationService() {
        final MongoRepo userDB = MongoRepo.getInstance();
        final String USER_COLLECTION_NAME = "users";
        this.userCollection = userDB.getUserCollection(USER_COLLECTION_NAME);
    }

    public Boolean register(RegisterData registerData) {
//todo username handeling
        if (null == registerData || null == registerData.password() || null == registerData.email()) {
            logger.severe("Register data is null");
            throw new IllegalArgumentException("Register data is null");
        }

        Optional<Document> foundDocument = Optional.ofNullable(userCollection.find(Filters.eq("mail", registerData.email())).first());

        if (foundDocument.isPresent()) {
            logger.severe("user already exists");
            throw new IllegalArgumentException("user already exists");
        } else {

            if (checkEmailAndPassword(registerData)) {

                final String hashedPassword = hashPassword(registerData.password());
                userCollection.insertOne(new Document(registerData.email(), hashedPassword));
                return true;

            } else  {
                logger.severe("Invalid email or password");
                throw new IllegalArgumentException("Invalid email or password");
            }
        }
    }

    private boolean checkEmailAndPassword(RegisterData registerData) {

        return (registerData.email().contains("@") &&
                registerData.password().length() > 8 &&
                hasLowerAndUpper(registerData.password()));
    }


    public static boolean hasLowerAndUpper(String password) {
        boolean hasLower = false;
        boolean hasUpper = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isUpperCase(c)) {
                hasUpper = true;
            }

            // Early exit if both found
            if (hasLower && hasUpper) {
                return true;
            }
        }
        return false;
    }

}
