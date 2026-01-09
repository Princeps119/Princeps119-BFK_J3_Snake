package data;

import java.util.UUID;

public record TokenData(String username, String encryptedMail, String timestamp, UUID version) {
}
