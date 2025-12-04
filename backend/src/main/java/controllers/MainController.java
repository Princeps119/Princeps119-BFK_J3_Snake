package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sun.net.httpserver.HttpExchange;
import data.LoginData;
import data.RegisterData;
import data.TokenData;
import services.LoginService;
import services.RegistrationService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainController {

    public static final Logger logger = Logger.getLogger(MainController.class.getName());

    private static final ArrayList<String> mapping = new ArrayList<>(Arrays.asList("/api/login", "/api/save", "/api/checkBackend", "/api/register"));

    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";

    private static final String CONTENT_TYPE_JSON = "application/json";

    public static Optional<Boolean> processRequest(final HttpExchange exchange) {
        try {

            final String method = exchange.getRequestMethod();

            final String path = exchange.getRequestURI().getPath();

            logger.log(Level.INFO, "Request Method: {0}, Path: {1}",
                    new Object[]{method, path});

            return Optional.of(checkMapping(path, method, exchange));

        } catch (IllegalArgumentException | IOException e) {
            //invalid mappings
            logger.log(Level.WARNING, "Error processing request", e);
            return Optional.of(false);
        }
    }

    private static Boolean checkMapping(final String path, final String method, final HttpExchange exchange) throws IllegalArgumentException, IOException {

        if (mapping.contains(path)) {
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

            }
        }
        throw new IllegalArgumentException("Invalid path");
    }


    private static Boolean register(final String method, final HttpExchange exchange) throws IllegalArgumentException {
        logger.log(Level.INFO, "in login Method");
        if (method.equals(POST) && exchange.getRequestHeaders().get("Content-Type").contains(CONTENT_TYPE_JSON)) {
            logger.log(Level.INFO, "in if in Login method");
            if (exchange.getRequestBody() != null) {

                final JsonReader reader = createJsonReader(exchange);
                final Gson gson = new GsonBuilder().create();
                final Type registerType = new TypeToken<RegisterData>() {
                }.getType();
                final RegisterData registerData = gson.fromJson(reader, registerType);
                final RegistrationService registrationService = RegistrationService.getInstance();

                try {
                     if (registrationService.register(registerData)) {
                         exchange.sendResponseHeaders(200, -1);
                         exchange.close();
                         return true;
                     }
                } catch (IllegalArgumentException | IOException e) {
                    logger.log(Level.INFO, "invalid register", e);
                }

            }
        }
        return false;
    }

    private static JsonReader createJsonReader(HttpExchange exchange) {
        final String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines()
                .collect(Collectors.joining("\n"));

        logger.log(Level.INFO, "got RequestBody: {0}", body);

        return new JsonReader(new StringReader(body));
    }


    //todo Robin implement logic and Services
    private static Boolean login(final String method, final HttpExchange exchange) throws IllegalArgumentException {
        logger.log(Level.INFO, "in login Method");
        if (method.equals(POST) && exchange.getRequestHeaders().get("Content-Type").contains(CONTENT_TYPE_JSON)) {
            logger.log(Level.INFO, "in if in Login method");
            if (exchange.getRequestBody() != null) {

                final JsonReader reader = createJsonReader(exchange);
                final Gson gson = new GsonBuilder().create();
                final Type loginType = new TypeToken<LoginData>() {}.getType();

                final LoginData loginData = gson.fromJson(reader, loginType);
                logger.log(Level.INFO, "did read JSON: {0}", loginData.mail());
                if (null != loginData) {

                    final String mail = loginData.mail();
                    final String password = loginData.password();
                    logger.log(Level.INFO, "got the login data: {0}", mail);
                    final LoginService loginService = LoginService.getInstance();

                    TokenData token = null;
                    try {
                        token = loginService.checkLoginData(mail, password);
                    } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException
                             | BadPaddingException | InvalidKeyException
                            e) {
                        logger.log(Level.WARNING, "failed to check login data", e);
                    }
                    if (null != token) {
                        //todo better not send a String but the Token directly

                        // Create Gson instance
                        Gson gson2 = new Gson();

                        // Convert to JSON
                        String json = gson2.toJson(token);
                        logger.log(Level.INFO, "sent token: " + json);

                        try {
                            exchange.sendResponseHeaders(200, json.getBytes().length);
                            exchange.getResponseBody().write(json.getBytes());
                            exchange.getResponseBody().close();
                            return true;
                        } catch (IllegalArgumentException | IOException e) {
                            logger.log(Level.WARNING, "failed to send token", e);
                        }

                    } else {
                        logger.log(Level.SEVERE, "Error in login, most likely with encryption for Logindata: " + mail + " " + password);
                    }

                } else {
                    logger.log(Level.SEVERE, "no user found loginData is null");
                    throw new IllegalArgumentException("No user found");
                }
            } else throw new IllegalArgumentException("Login data is null");

        } else throw new IllegalArgumentException("Login data is null");

        return null;
    }

    private static Boolean save(final String method, HttpExchange exchange) {

        return false;
    }

    //todo think if any of these logic methods even have to have a return type or just do void and handle the exchange inside the method itself
    //instead of the main method
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
