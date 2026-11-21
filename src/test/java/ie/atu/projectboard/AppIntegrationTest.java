package ie.atu.projectboard;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

// Integration tests: start the real web server and send real HTTP requests to it.
public class AppIntegrationTest {

    private static HttpServer server;
    private static String baseUrl;
    private static HttpClient client;

    // Runs once before all tests: start the server on a free port
    @BeforeAll
    public static void startServer() throws Exception {
        server = App.startServer(0);
        int port = server.getAddress().getPort();
        baseUrl = "http://localhost:" + port;
        client = HttpClient.newHttpClient();
    }

    // Runs once after all tests: stop the server
    @AfterAll
    public static void stopServer() {
        server.stop(0);
    }

    // Sends a GET request and returns the response
    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // Sends a POST request with form data, like a browser submitting the form
    private HttpResponse<String> post(String path, String formData) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void getHomePageReturnsStudentDetails() throws Exception {
        HttpResponse<String> response = get("/");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Student Details"));
        assertTrue(response.body().contains("About the Project"));
        assertTrue(response.headers().firstValue("Content-Type").orElse("").contains("text/html"));
    }

    @Test
    public void getCommentsPageShowsForm() throws Exception {
        HttpResponse<String> response = get("/comments");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("<form"));
        assertTrue(response.body().contains("name=\"email\""));
    }

    @Test
    public void getUnknownPageReturns404() throws Exception {
        HttpResponse<String> response = get("/does-not-exist");
        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("Page not found"));
    }

    @Test
    public void postValidCommentIsSavedAndShown() throws Exception {
        HttpResponse<String> response = post("/comments",
                "name=Integration+Tester&email=tester%40example.com&comment=Great+project");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Thank you, your comment was added."));
        assertTrue(response.body().contains("Integration Tester"));

        // A new GET request should still show the saved comment
        HttpResponse<String> page = get("/comments");
        assertTrue(page.body().contains("Integration Tester"));
        assertTrue(page.body().contains("Great project"));
    }

    @Test
    public void postWithoutNameShowsError() throws Exception {
        HttpResponse<String> response = post("/comments",
                "name=&email=tester%40example.com&comment=Hello");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Please enter your name."));
    }

    @Test
    public void postWithBadEmailShowsError() throws Exception {
        HttpResponse<String> response = post("/comments",
                "name=Tester&email=not-an-email&comment=Hello");
        assertTrue(response.body().contains("Please enter a valid email."));
    }

    @Test
    public void postWithEmptyBodyShowsError() throws Exception {
        HttpResponse<String> response = post("/comments", "");
        assertTrue(response.body().contains("Please enter your name."));
    }

    @Test
    public void postedHtmlIsEscaped() throws Exception {
        HttpResponse<String> response = post("/comments",
                "name=Hacker&email=h%40example.com&comment=%3Cscript%3Ealert(1)%3C%2Fscript%3E");
        assertTrue(response.body().contains("&lt;script&gt;"));
        assertFalse(response.body().contains("<script>alert(1)</script>"));
    }
}
