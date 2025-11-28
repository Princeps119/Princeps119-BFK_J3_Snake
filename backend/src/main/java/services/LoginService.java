package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import exceptions.UserNotFoundException;
import org.bson.Document;
import repository.MongoRepo;

import java.util.Optional;

public class LoginService {


    private static LoginService instance;
    private static final MongoRepo userDB = MongoRepo.getInstance();
    private static final String USER_COLLECTION_NAME = "users";

    MongoCollection<Document> userCollection = userDB.getUserCollection(USER_COLLECTION_NAME);

    public static synchronized LoginService getInstance() {
        if (instance == null) {
            instance = new LoginService();
        }
        return instance;
    }

    public String checkLoginData(final String mail, final String password) {

        Document foundDocument = userCollection.find(Filters.eq("mail", mail)).first();

        if (foundDocument != null) {
            final Optional<String> hashedPassword = Optional.ofNullable(foundDocument.get("hashedPassword").toString());
            final Optional<String> dbMail = Optional.ofNullable(foundDocument.get("mail").toString());

            if (hashedPassword.isPresent() && dbMail.isPresent()) {
                if (hashedPassword.get().equals(password) && mail.equals(dbMail.get())) {
                    Optional<String> username = Optional.ofNullable(foundDocument.get("username").toString());

                    // todo create token with uid and mail and timestamp to be saved in different collection
                    // send it to the frontend, upon, /save /load /delete check that token, if not older than
                    // 4 hours assume user is authenticated

                    return username.map(String::toString).orElse("authenticated");
                }
            }
        }
        throw new UserNotFoundException("no document found");
    }
}
