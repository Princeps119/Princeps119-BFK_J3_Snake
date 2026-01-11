package controllers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import static services.Util.*;

public class FrontendController {

    private static final Path STATIC_ROOT = Path.of("/opt/exec/frontend");

    private static final ArrayList<String> mapping = new ArrayList<>(Arrays.asList("/login", "/register", "/snake"));

    public static void serveStaticFromRoot(HttpExchange exchange) throws IOException {
        final String method = exchange.getRequestMethod();
        final String urlPath = exchange.getRequestURI().getPath();

        logger.log(Level.INFO, "Processing path: {0}", urlPath);

        // as only html pages should be served, erlaube nur GET
        if (!"GET".equalsIgnoreCase(method)) {
            sendErrorResponse(exchange, 405, "Method Not Allowed");
        }

        serveHTMLPages(exchange, urlPath);
    }

    private static void serveHTMLPages(final HttpExchange exchange, final String mappedPath) throws IOException {

        if (mappedPath.endsWith(".css") || mappedPath.endsWith(".js") || mappedPath.endsWith(".webp")) {
            logger.log(Level.INFO, "Processing asset file for path: {0}", mappedPath);
            serveAssetPages(mappedPath, exchange);
            return;
        }

        Path file = STATIC_ROOT.resolve(mappedPath.replace("/", "")).resolve(mappedPath.replace("/", "") + ".html").normalize();
        if (!file.startsWith(STATIC_ROOT) || !Files.isRegularFile(file)) {
            sendErrorResponse(exchange, 404, "Not Found: " + file);
            return;
        }

        byte[] responseBytes = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private static void serveAssetPages(String path, HttpExchange exchange) throws IOException {

        // Remove leading slash and resolve
        String relativePath = path.startsWith("/") ? path.substring(1) : path;
        Path file = STATIC_ROOT.resolve(relativePath).normalize();

        if (!file.startsWith(STATIC_ROOT) || !Files.isRegularFile(file)) {
            logger.log(Level.WARNING, "asset file for path: {0} not found", relativePath);
            sendErrorResponse(exchange, 404, "Not Found: " + file);
            return;
        }

        byte[] body = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", guessContentType(file.toString()));
        exchange.sendResponseHeaders(200, body.length);
        try (var os = exchange.getResponseBody()) {
            os.write(body);
        }
    }
}
