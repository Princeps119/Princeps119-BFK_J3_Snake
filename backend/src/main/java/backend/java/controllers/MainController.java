package backend.java.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class MainController {

    private static final ArrayList<String> mapping = new ArrayList<>(Arrays.asList("/login", "/save", "/checkBackend"));

    private static final String POST = "POST";
    private static final String GET = "GET";

    public static Optional<String> processRequest(Socket socket) throws IOException {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request = reader.readLine();

            // split request and parse it.
            final String[] parts = request.split(" ");
            final String method = parts[0];
            final String path = parts[1];

            //todo Robin handle return value
            checkMapping(path, method);

        } catch (IOException e) {
            socket.close();
        } catch (IllegalArgumentException e){
            return Optional.of(e.getMessage());
        }

        return Optional.empty();
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
            return "alive";
        } else {
            throw new IllegalArgumentException("Invalid method");
        }
    }
}
