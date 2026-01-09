package data;

import java.util.UUID;
/**
 * Represents the current state of the snake in the game.
 *
 * @param username not unique and not used in backend
 * @param encryptedMail Mail of user encrypted with AES as it is sent back to frontend
 * @param timestamp encrypted Instance.now() which is checked in all backendrequest where auth is needed if older than 4h the request is declined
 * @param version UUID used on logout the users saved version attribute of the token in mongoDB is changed -> all requests where the request uuid is not the
 *                same as the one saved in MongoDB are declined. (This enables logout via the token and prevents an old saved token to be used)
 */
public record TokenData(String username, String encryptedMail, String timestamp, UUID version) {
}
