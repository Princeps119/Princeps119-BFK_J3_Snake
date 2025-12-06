package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import data.RegisterData;
import org.bson.Document;
import repository.MongoRepo;

import java.util.Date;
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

        if (null == registerData || null == registerData.password() || null == registerData.mail()) {
            logger.severe("Register data is null");
            throw new IllegalArgumentException("Register data is null");
        }

        Optional<Document> foundDocument = Optional.ofNullable(userCollection.find(Filters.eq("mail", registerData.mail())).first());

        if (foundDocument.isPresent()) {
            logger.severe("user already exists");
            throw new IllegalArgumentException("user already exists");
        } else {

            if (checkEmailAndPassword(registerData)) {

                final String hashedPassword = hashPassword(registerData.password());
                final Document doc = new Document()
                        .append("username", registerData.username())
                        .append("mail", registerData.mail())
                        .append("hashedPassword", hashedPassword)
                        .append("createdAt", new Date());
                userCollection.insertOne(doc);

                return true;

            } else  {
                logger.severe("Invalid mail or password");
                throw new IllegalArgumentException("Invalid mail or password");
            }
        }
    }

    private boolean checkEmailAndPassword(RegisterData registerData) {

        return (registerData.mail().contains("@") &&
                registerData.mail().length() < 25 &&
                registerData.username().length() < 20 &&
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
