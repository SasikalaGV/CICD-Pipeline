package ie.atu.projectboard;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests run by Maven ("mvn test")
public class AppTest {

    @Test
    public void homePageShowsStudentDetails() {
        String html = Pages.homePage();
        assertTrue(html.contains("Student Details"));
        assertTrue(html.contains(Pages.STUDENT_NAME));
        assertTrue(html.contains("About the Project"));
    }

    @Test
    public void commentsPageHasFormFields() {
        String html = Pages.commentsPage(new ArrayList<>(), "");
        assertTrue(html.contains("name=\"name\""));
        assertTrue(html.contains("name=\"email\""));
        assertTrue(html.contains("name=\"comment\""));
        assertTrue(html.contains("No comments yet."));
    }

    @Test
    public void commentsPageListsComments() {
        List<Comment> list = new ArrayList<>();
        list.add(new Comment("Ann", "ann@example.com", "Nice project"));
        String html = Pages.commentsPage(list, "");
        assertTrue(html.contains("Ann"));
        assertTrue(html.contains("Nice project"));
    }

    @Test
    public void validateAcceptsGoodInput() {
        assertNull(Pages.validate("Ann", "ann@example.com", "Hello"));
    }

    @Test
    public void validateRejectsBadInput() {
        assertNotNull(Pages.validate("", "ann@example.com", "Hello"));
        assertNotNull(Pages.validate("Ann", "not-an-email", "Hello"));
        assertNotNull(Pages.validate("Ann", "ann@example.com", ""));
    }

    @Test
    public void escapeStopsHtml() {
        assertEquals("&lt;b&gt;hi&lt;/b&gt;", Pages.escape("<b>hi</b>"));
    }

    @Test
    public void parseFormReadsValues() {
        Map<String, String> form = App.parseForm("name=Ann+Lee&email=ann%40example.com&comment=Hi");
        assertEquals("Ann Lee", form.get("name"));
        assertEquals("ann@example.com", form.get("email"));
        assertEquals("Hi", form.get("comment"));
    }
}
