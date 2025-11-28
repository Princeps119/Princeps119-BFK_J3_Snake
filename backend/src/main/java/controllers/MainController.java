package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.sun.net.httpserver.HttpExchange;
import data.LoginData;
import org.bson.Document;
import repository.MongoRepo;
import services.LoginService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Type;
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

    public static Optional<String> processRequest(final HttpExchange exchange) {
        try {

            final String method = exchange.getRequestMethod();

            final String path = exchange.getRequestURI().getPath();

            logger.log(Level.INFO, "Request Method: {0}, Path: {1}",
                    new Object[]{method, path});

            return Optional.of(checkMapping(path, method, exchange));

        } catch (IllegalArgumentException | IOException e) {
            //invalid mappings
            return Optional.of(e.getMessage());
        }
    }

    private static String checkMapping(final String path, final String method, final HttpExchange exchange) throws IllegalArgumentException, IOException {

        if (mapping.contains(path)) {

            final int mappedPath = mapping.indexOf(path);
            switch (mappedPath) {
                case 0:
                    return login(method, exchange);
                case 1:
                    return save(method, exchange);
                case 2:
                    return checkBackend(method);
                case 3:
                    return register(method, exchange);

            }
        }
        throw new IllegalArgumentException("Invalid path");
    }


    private static String register(final String method, final HttpExchange exchange) throws IllegalArgumentException {
        return null;
    }


    //todo Robin implement logic and Services
    private static String login(final String method, final HttpExchange exchange) throws IllegalArgumentException {

        if (method.equals(POST) && exchange.getRequestHeaders().containsValue(CONTENT_TYPE_JSON)) {

            if (exchange.getRequestBody() != null) {
                final String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                        .lines()
                        .collect(Collectors.joining("\n"));

                final Gson gson = new GsonBuilder().create();

                final JsonReader reader = new JsonReader(new StringReader(body));
                final Type loginType = new TypeToken<LoginData>() {
                }.getType();
                final LoginData loginData = gson.fromJson(reader, loginType);

                if (null != loginData) {

                    final String mail = loginData.mail();
                    final String password = loginData.password();

                    //todo check mail and pw against DB
                    return LoginService.getInstance().checkLoginData(mail, password);

                } else {
                    logger.log(Level.SEVERE, "no user found loginData is null");
                    throw new IllegalArgumentException("No user found");
                }


            } else throw new IllegalArgumentException("Login data is null");

        }
        return null;
    }

    private static String save(final String method, HttpExchange exchange) {

        return "todo";
    }

    //todo think if any of these logic methods even have to have a return type or just do void and handle the exchange inside the method itself
    //instead of the main method
    private static String checkBackend(final String method) throws IllegalArgumentException {

        if (method.equals(GET)) {
            return "alive";

        } else {
            throw new IllegalArgumentException("Invalid method");
        }
    }
}
