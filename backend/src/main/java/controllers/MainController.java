package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import data.LoginData;
import data.RegisterData;
import data.SnakePositionData;
import data.TokenData;
import exceptions.EncryptionException;
import exceptions.UserNotFoundException;
import services.DeletionService;
import services.LoginService;
import services.RegistrationService;
import services.SaveGameService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static services.Util.*;

public class MainController {

    public static final Logger logger = Logger.getLogger(MainController.class.getName());

    private static final ArrayList<String> mapping = new ArrayList<>(Arrays.asList("/api/login", "/api/save", "/api/checkBackend",
            "/api/register", "/api/delete", "/api/load", "/api/logout",
            // html pages

            "/login", "/register", "/snake"));

    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PATCH = "PATCH";
    public static final String DELETE = "DELETE";

    public static final Path STATIC_ROOT = Path.of("/opt/exec/frontend");

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static Optional<Boolean> processRequest(final HttpExchange exchange) {
        try {

            final String method = exchange.getRequestMethod();

            final String path = exchange.getRequestURI().getPath();

            logger.log(Level.INFO, "Request Method: {0}, Path: {1}",
                    new Object[]{method, path});

            if (path.endsWith(".css") || path.endsWith(".js")) {
                return Optional.of(serveAssetPages(path, exchange));
            }
            return Optional.of(checkMapping(path, method, exchange));

        } catch (IllegalArgumentException | IOException e) {
            //invalid mappings
            logger.log(Level.WARNING, "Error processing request", e);
            throw new IllegalArgumentException("Error processing request", e);
        }
    }

    private static Boolean serveAssetPages(String path, HttpExchange exchange) throws IOException {

        // Remove leading slash and resolve
        String relativePath = path.startsWith("/") ? path.substring(1) : path;
        Path file = STATIC_ROOT.resolve(relativePath).normalize();

        if (!file.startsWith(STATIC_ROOT) || !Files.isRegularFile(file)) {
            sendErrorResponse(exchange, 404, "Not Found: " + file);
            return false;
        }

        byte[] body = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", guessContentType(file.toString()));
        exchange.sendResponseHeaders(200, body.length);
        try (var os = exchange.getResponseBody()) {
            os.write(body);
        }
        return true;
    }

    private static Boolean checkMapping(final String path, final String method, final HttpExchange exchange) throws IllegalArgumentException, IOException {

        final String checkedPath = checkPath(path, exchange);

        if (mapping.contains(checkedPath)) {
            logger.log(Level.INFO, "Mapping found for {0}", path);
            final int mappedPath = mapping.indexOf(path);
            switch (mappedPath) {
                case 0:
                    return login(method, exchange);
                case 1:
                    return save(method, exchange);
                case 2:
                    return checkBackend(method, exchange);
                case 3:
                    return register(method, exchange);
                case 4:
                    return delete(method, exchange);
                case 5:
                    return load(method, exchange);
                case 6:
                    return logout(method, exchange);
                case 7, 8, 9:
                    return serveHTMLPages(method, exchange, checkedPath);
            }
        }
        throw new IllegalArgumentException("Invalid path");
    }

    private static Boolean serveHTMLPages(final String method, final HttpExchange exchange, final String mappedPath) throws IOException {
        if (!GET.equalsIgnoreCase(method)) {
            sendErrorResponse(exchange, 405, "Method Not Allowed");
            return false;
        }

        Path file = STATIC_ROOT.resolve(mappedPath.replace("/", "")).resolve(mappedPath.replace("/", "") + ".html").normalize();
        if (!file.startsWith(STATIC_ROOT) || !Files.isRegularFile(file)) {
            sendErrorResponse(exchange, 404, "Not Found: " + file);
            return false;
        }

        byte[] responseBytes = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
        return true;
    }

    private static boolean logout(String method, HttpExchange exchange) {
        try {
            if (method.equals(POST)) {
                final TokenData loginToken = readJSON(exchange, TokenData.class);
                final LoginService loginService = LoginService.getInstance();
                final boolean result = loginService.logout(loginToken);
                if (result) {
                    exchange.sendResponseHeaders(200, -1);
                    exchange.close();
                    return true;
                } else  {
                    sendErrorResponse(exchange, 500, "Error processing logout request");
                }
            }
        } catch (EncryptionException | IOException e) {
            logger.log(Level.WARNING, "Error processing logout request", e);
            sendErrorResponse(exchange, 500, "Error processing logout request");
        }

        return false;
    }

    private static Boolean delete(final String method, final HttpExchange exchange) {

        if (method.equals(DELETE)) {
            try {
                final DeletionService deletionService = DeletionService.getInstance();
                final boolean didDelete = deletionService.deleteUser(exchange);
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return didDelete;
            } catch (IOException e) {
                sendErrorResponse(exchange, 500, "Error deleting user");

            }
        }
        return false;
    }

    private static Boolean register(final String method, final HttpExchange exchange) throws IllegalArgumentException {
        if (method.equals(POST) && exchange.getRequestHeaders().get("Content-Type").contains(CONTENT_TYPE_JSON)) {
            if (exchange.getRequestBody() != null) {
                try {
                    final RegisterData registerData = readJSON(exchange, RegisterData.class);
                    final RegistrationService registrationService = RegistrationService.getInstance();

                    if (registrationService.register(registerData)) {
                        exchange.sendResponseHeaders(204, -1);
                        exchange.close();
                        return true;
                    }

                } catch (IllegalArgumentException e) {
                    logger.log(Level.WARNING, "illegal argument", e);
                    sendErrorResponse(exchange, 400, e.getMessage());

                } catch (JsonSyntaxException e) {
                    logger.log(Level.WARNING, "JsonSyntaxException", e);
                    sendErrorResponse(exchange, 400, "Invalid JSON format");

                } catch (Exception e) {
                    logger.log(Level.WARNING, "Exception", e);
                    sendErrorResponse(exchange, 500, "Internal server error");
                }
            } else {
                sendErrorResponse(exchange, 400, "Request body is required");
            }
        }
        logger.log(Level.WARNING, "no exception, and no success either");
        return false;
    }

    private static boolean login(final String method, final HttpExchange exchange) {

        if (!validateInputs(method, exchange)) {
            return false;
        }

        try {

            // Authenticate user
            final LoginService loginService = LoginService.getInstance();
            TokenData token;

            final LoginData loginData = loginService.getMailAndPassword(exchange);

            try {
                token = loginService.checkLoginData(loginData.mail(), loginData.password());
            } catch (NoSuchPaddingException | IllegalBlockSizeException |
                     NoSuchAlgorithmException | BadPaddingException |
                     InvalidKeyException e) {
                logger.log(Level.WARNING, "Encryption error during login", e);
                sendErrorResponse(exchange, 500, "Authentication service error");
                return false;
            }

            if (token == null) {
                sendErrorResponse(exchange, 401, "Invalid email or password");
                return false;
            }

            // Success - send token
            final Gson gson = new GsonBuilder().create();
            final String jsonResponse = gson.toJson(token);
            final byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }

            logger.log(Level.INFO, "Login successful for: {0}", loginData.mail());
            return true;

        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Invalid JSON in login request", e);
            sendErrorResponse(exchange, 400, "Invalid JSON format");
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during login", e);
            sendErrorResponse(exchange, 500, "Internal server error");
            return false;
        } finally {
            exchange.close();
        }
    }

    private static Boolean save(final String method, HttpExchange exchange) {
        if (method.equals(PATCH) && exchange.getRequestHeaders().get("Content-Type").contains(CONTENT_TYPE_JSON)) {
            if (exchange.getRequestBody() != null) {
                try {
                    final SaveGameService saveService = SaveGameService.getInstance();
                    final SnakePositionData snakeData = readJSON(exchange, SnakePositionData.class);
                    return saveService.saveSnakePosition(exchange, snakeData);

                } catch (UserNotFoundException e) {
                    sendErrorResponse(exchange, 400, "User not found");
                }
            }
            return false;
        }
        return false;
    }

    //needs the LoginToken in a Header
    private static Boolean load(final String method, final HttpExchange exchange) {
        if (method.equals(GET)) {
            try {
                final SaveGameService saveService = SaveGameService.getInstance();

                final SnakePositionData snakeData = saveService.loadGame(exchange);

                final Gson gson = new GsonBuilder().create();
                final String json = gson.toJson(snakeData);
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(200, bytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
                return true;

            } catch (UserNotFoundException e) {
                sendErrorResponse(exchange, 500, "User not found");
            } catch (IllegalArgumentException e) {
                sendErrorResponse(exchange, 400, "Invalid username or password");
            } catch (IOException e) {
                sendErrorResponse(exchange, 500, "Internal server error");
            }
            return false;
        }
        return false;
    }

    private static Boolean checkBackend(final String method, final HttpExchange exchange) throws IllegalArgumentException {

        if (method.equals(GET)) {
            try {
                exchange.sendResponseHeaders(200, -1);
                exchange.close();
                return true;
            } catch (IOException e) {
                logger.log(Level.WARNING, "invalid request", e);
            }

        } else {
            throw new IllegalArgumentException("Invalid method");
        }
        return false;
    }
}
