package ie.atu.projectboard;

import java.util.List;

// Builds the HTML for each page as a simple String.
public class Pages {

    // ---- Change these to your own details ----
    public static final String STUDENT_NAME = "Sasikala Varatharajan";
    public static final String STUDENT_ID = "L00213592";
    public static final String COURSE = "PGDip in Computing in DevOps";
    public static final String COLLEGE = "Atlantic Technological University (ATU)";
    public static final String PROJECT_NOTE =
            "ATU Project Board is a small Java web application built for my DevOps module. "
          + "It shows my student details and lets visitors leave comments on the project. "
          + "The application is used to demonstrate a CI/CD pipeline."
          + "(deployed automatically by GitHub Actions)";
    // -------------------------------------------

    // Home page: student details and project note
    public static String homePage() {
        return "<html><head><title>ATU Project Board</title></head><body>"
             + "<h1>ATU Project Board</h1>"
             + "<p><a href=\"/\">Home</a> | <a href=\"/comments\">Comments</a></p>"
             + "<h2>Student Details</h2>"
             + "<table border=\"1\" cellpadding=\"5\">"
             + "<tr><td>Name</td><td>" + STUDENT_NAME + "</td></tr>"
             + "<tr><td>Student ID</td><td>" + STUDENT_ID + "</td></tr>"
             + "<tr><td>Course</td><td>" + COURSE + "</td></tr>"
             + "<tr><td>College</td><td>" + COLLEGE + "</td></tr>"
             + "</table>"
             + "<h2>About the Project</h2>"
             + "<p>" + PROJECT_NOTE + "</p>"
             + "</body></html>";
    }

    // Comments page: a form, then the list of comments so far
    public static String commentsPage(List<Comment> comments, String message) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Comments</title></head><body>");
        html.append("<h1>Comments</h1>");
        html.append("<p><a href=\"/\">Home</a> | <a href=\"/comments\">Comments</a></p>");

        if (message != null && !message.isEmpty()) {
            html.append("<p><b>").append(escape(message)).append("</b></p>");
        }

        html.append("<h2>Leave a comment</h2>");
        html.append("<form method=\"post\" action=\"/comments\">");
        html.append("Name:<br><input type=\"text\" name=\"name\"><br><br>");
        html.append("Email:<br><input type=\"email\" name=\"email\"><br><br>");
        html.append("Comment:<br><textarea name=\"comment\" rows=\"4\" cols=\"40\"></textarea><br><br>");
        html.append("<input type=\"submit\" value=\"Submit\">");
        html.append("</form>");

        html.append("<h2>All comments</h2>");
        if (comments.isEmpty()) {
            html.append("<p>No comments yet.</p>");
        } else {
            for (Comment c : comments) {
                html.append("<p><b>").append(escape(c.getName())).append("</b>");
                html.append(" (").append(escape(c.getEmail())).append(")<br>");
                html.append(escape(c.getText())).append("</p><hr>");
            }
        }

        html.append("</body></html>");
        return html.toString();
    }

    // Checks the form. Returns an error message, or null if everything is OK.
    public static String validate(String name, String email, String comment) {
        if (name == null || name.trim().isEmpty()) {
            return "Please enter your name.";
        }
        if (email == null || !email.contains("@")) {
            return "Please enter a valid email.";
        }
        if (comment == null || comment.trim().isEmpty()) {
            return "Please enter a comment.";
        }
        return null;
    }

    // Stops people typing HTML/JavaScript into the comment box
    public static String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}
