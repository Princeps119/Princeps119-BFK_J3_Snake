package exceptions;

public class EncryptionException extends RuntimeException {

    final String message;

    public EncryptionException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "User not found";
    }

}


