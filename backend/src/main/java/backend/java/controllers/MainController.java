package backend.java.controllers;

import com.sun.net.httpserver.HttpExchange;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {

    public static final Logger logger = Logger.getLogger(MainController.class.getName());

    private static final ArrayList<String> mapping = new ArrayList<>(Arrays.asList("/login", "/save", "/checkBackend"));

    private static final String POST = "POST";
    private static final String GET = "GET";

    public static Optional<String> processRequest(final HttpExchange exchange) {
        try {

            final String method = exchange.getRequestMethod();

            final String path = exchange.getRequestURI().getPath();

            logger.log(Level.INFO, "Request Method: {0}, Path: {1}",
                    new Object[]{method, path});

            return Optional.of(checkMapping(path, method));

        } catch (IllegalArgumentException e) {
            //invalid mappings
            return Optional.of(e.getMessage());
        }
    }

    private static String checkMapping(final String path, final String method) throws IllegalArgumentException {

            if (mapping.contains(path)) {

                final int mappedPath = mapping.indexOf(path);
                switch (mappedPath) {
                    case 0:
                        return login(method);
                    case 1:
                        return save(method);
                    case 2:
                        return checkBackend(method);

                }
            }
        throw new IllegalArgumentException("Invalid path");
    }

    //todo Robin implement logic and Services
    private static String login(final String method) {

        return "todo";
    }

    private static String save(final String method) {

        return "todo";
    }

    private static String checkBackend(final String method) {

        if (method.equals(GET)) {
            final String content = "alive";

            final String headers = String.format(
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/plain; charset=utf-8\r\n" +
                            "Content-Length: %d\r\n" +
                            "\r\n",
                    content.getBytes(StandardCharsets.UTF_8).length
            );

            return headers + content;

        } else {
            throw new IllegalArgumentException("Invalid method");
        }
    }
}
