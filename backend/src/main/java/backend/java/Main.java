package backend.java;


import backend.java.controllers.MainController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static final int PORT = 8080;

    public static void main( String[] args) throws IOException {

        try {
            // Start HTTP server on port 8080, should stay open, warning can be ignored
            final ServerSocket serverSocket = new ServerSocket(PORT);

            //run in perpetuity, warning can be ignored
            while (true) {

                Socket socket = serverSocket.accept();

                new Thread(() -> {
                    try {

                        Optional<String> response = MainController.processRequest(socket);

                        if (response.isPresent()) {
                            socket.getOutputStream().write(response.get().getBytes());
                        }

                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Error processing request", e);
                    }
                }).start();
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error accepting connection", ex);
        }
    }
}