package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sun.net.httpserver.HttpExchange;
import data.TokenData;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static controllers.MainController.CONTENT_TYPE_JSON;
import static controllers.MainController.POST;

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

    public static boolean validateInputs(String method, HttpExchange exchange) {
        // Validate request method first
        if (!POST.equals(method)) {
            sendErrorResponse(exchange, 405, "Method not allowed. Use POST.");
            return false;
        }

        // Validate Content-Type header
        List<String> contentTypes = exchange.getRequestHeaders().get("Content-Type");
        if (contentTypes == null || !contentTypes.contains(CONTENT_TYPE_JSON)) {
            sendErrorResponse(exchange, 415, "Content-Type must be application/json");
            return false;
        }

        // Validate request body exists
        if (exchange.getRequestBody() == null) {
            sendErrorResponse(exchange, 400, "Request body is required");
            return false;
        }
        return true;
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

            // Set proper headers, including CORS so browsers accept the response, see Main, do not need to set them again here
            // or new cors as the original header will not be removed
            var headers = exchange.getResponseHeaders();
            if (headers.containsKey("Content-Type")) {
                headers.get("Content-Type").remove(CONTENT_TYPE_JSON);
            }
            headers.set("Content-Type", "application/problem+json");
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

    public static String checkLoginToken(HttpExchange exchange) {
        //check timestamp and LoginToken, if null is returned an error happened
        final String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(exchange, 401, "Missing or invalid Authorization header");
            return null;
        }

        final String tokenJson = authHeader.substring("Bearer ".length()).trim();

        TokenData tokenData;
        try {
            final JsonReader reader = new JsonReader(new StringReader(tokenJson));
            final Gson gson = new GsonBuilder().create();
            final Type tokenType = new TypeToken<TokenData>() {
            }.getType();

            tokenData = gson.fromJson(reader, tokenType);

        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Malformed token");
            return null;
        }

        String decryptedMail;
        String decryptedTimestamp;
        try {
            decryptedMail = TokenEncrypter.decrypt(tokenData.encryptedMail());
            decryptedTimestamp = TokenEncrypter.decrypt(tokenData.timestamp());
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid token encryption");
            return null;
        }

        // check timestamp
        try {
            Instant tokenTime = Instant.parse(decryptedTimestamp);
            if (Duration.between(tokenTime, Instant.now()).toHours() > 4) {
                sendErrorResponse(exchange, 401, "Token expired");
                return null;
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 400, "Invalid timestamp format");
            return null;
        }

        return decryptedMail;
    }
}
