package ie.atu.projectboard;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Starts a small web server using the HTTP server built into Java.
// Open http://localhost:8080 in a browser after running it.
public class App {

    // Comments are kept in memory, so they are lost when the app stops.
    private static List<Comment> comments = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Home page
        server.createContext("/", exchange -> {
            if (!exchange.getRequestURI().getPath().equals("/")) {
                send(exchange, 404, "<html><body><h1>Page not found</h1><a href=\"/\">Home</a></body></html>");
                return;
            }
            send(exchange, 200, Pages.homePage());
        });

        // Comments page (GET shows the form, POST saves a comment)
        server.createContext("/comments", exchange -> {
            String message = "";

            if (exchange.getRequestMethod().equals("POST")) {
                Map<String, String> form = readForm(exchange);
                String name = form.get("name");
                String email = form.get("email");
                String text = form.get("comment");

                String error = Pages.validate(name, email, text);
                if (error == null) {
                    comments.add(new Comment(name, email, text));
                    message = "Thank you, your comment was added.";
                } else {
                    message = error;
                }
            }

            send(exchange, 200, Pages.commentsPage(comments, message));
        });

        server.start();
        System.out.println("ATU Project Board running at http://localhost:" + port);
    }

    // Sends an HTML page back to the browser
    private static void send(HttpExchange exchange, int status, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream out = exchange.getResponseBody();
        out.write(bytes);
        out.close();
    }

    // Reads the submitted form, e.g. "name=Ann&email=a%40b.com&comment=Hi"
    private static Map<String, String> readForm(HttpExchange exchange) throws IOException {
        InputStream in = exchange.getRequestBody();
        String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        return parseForm(body);
    }

    // Turns "a=1&b=2" into a map. Public so it can be unit tested.
    public static Map<String, String> parseForm(String body) {
        Map<String, String> form = new HashMap<>();
        if (body == null || body.isEmpty()) {
            return form;
        }
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            form.put(key, value);
        }
        return form;
    }
}
