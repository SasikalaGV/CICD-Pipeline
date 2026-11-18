package ie.atu.projectboard;

// One comment left by a visitor on the Comments page.
public class Comment {

    private String name;
    private String email;
    private String text;

    public Comment(String name, String email, String text) {
        this.name = name;
        this.email = email;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getText() {
        return text;
    }
}
