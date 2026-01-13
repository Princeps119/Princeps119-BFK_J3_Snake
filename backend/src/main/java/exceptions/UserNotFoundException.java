package exceptions;

public class UserNotFoundException extends RuntimeException {
    final String message;

    public UserNotFoundException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "User not found";
    }
}
