package services;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    public static final Logger logger = Logger.getLogger(Util.class.getName());

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Error while encrypting password", e);
        }
        return null;
    }

    public static void sendErrorResponse(final HttpExchange exchange,
                                         final int statusCode,
                                         final String errorMsg) {
        try (exchange) {
            // Create structured JSON error response following RFC 7807
            String jsonResponse = String.format(
                    "{\"type\": \"about:blank\", \"title\": \"Error\", \"status\": %d, \"detail\": \"%s\"}",
                    statusCode, errorMsg.replace("\"", "\\\""));

            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

            // Set proper headers
            exchange.getResponseHeaders().set("Content-Type", "application/problem+json");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);

            // Use try-with-resources to prevent leaks
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }

            logger.log(Level.WARNING, "Sent error response: {0} - {1}",
                    new Object[]{statusCode, errorMsg});

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send error response", e);
        }
    }
}
