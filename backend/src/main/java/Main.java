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

           // CORS: add headers for all responses, as test with Postman did work, but the Browsertest did not
           var headers = exchange.getResponseHeaders();
           headers.add("Access-Control-Allow-Origin", "*"); // Erlaubt Anfragen von allen Domains in productive noch auf domain einschränken
           headers.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"); // Gibt an, welche HTTP-Methoden für Cross-Origin-Anfragen erlaubt sind.
           headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization"); // Definiert, welche benutzerdefinierten Header in der Anfrage erlaubt sind.
           headers.add("Access-Control-Max-Age", "3600"); // Gibt an, wie lange das Ergebnis des Preflight-Checks (OPTIONS) gecacht werden darf (in Sekunden).

           // needed as for POST a browser may send an Options first to check connection
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

           try {
               final Optional<Boolean> processedRequest = MainController.processRequest(exchange);
               if (processedRequest.isPresent() && processedRequest.get()) {
                   logger.log(Level.INFO, "Request processed");
               }
           } catch (Exception e) {
               exchange.sendResponseHeaders(404, -1);
               exchange.close();
           }
        });

        server.setExecutor(null);
        server.start();
        logger.info("Server running on port " + PORT);
    }
}