import com.sun.net.httpserver.HttpServer;
import controllers.MainController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", exchange -> {

           try {
               final Optional<Boolean> processedRequest = MainController.processRequest(exchange);
               if (processedRequest.isPresent() && processedRequest.get() == true) {
                   logger.log(Level.INFO, "Request processed");
               }
           } catch (Exception e) {
               exchange.sendResponseHeaders(404, -1);
           }
        });

        server.setExecutor(null);
        server.start();
        logger.info("Server running on port " + PORT);
    }
}