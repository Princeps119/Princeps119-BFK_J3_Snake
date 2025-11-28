import com.sun.net.httpserver.HttpServer;
import controllers.MainController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.logging.Logger;

public class Main {

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", exchange -> {

            Optional<String> response = MainController.processRequest(exchange);

            if (response.isPresent() && !response.get().equals("error")) {
                exchange.sendResponseHeaders(200, response.get().getBytes().length);
                exchange.getResponseBody().write(response.get().getBytes());
            } else  {
                exchange.sendResponseHeaders(404, 0);
            }
        });

        server.setExecutor(null);
        server.start();
        logger.info("Server running on port " + PORT);
    }
}