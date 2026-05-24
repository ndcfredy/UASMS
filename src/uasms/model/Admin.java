package uasms.model;

import uasms.util.Colors;
import java.io.Serializable;

/**
 * Admin.java
 * ─────────────────────────────────────────────────────
 * Administrator account. Can add/remove students,
 * lecturers, courses and reset passwords.
 * ─────────────────────────────────────────────────────
 */
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String lastName;    // UPPERCASE
    private String firstName;   // Normal case
    private String username;

    public Admin(String id, String title, String lastName,
                 String firstName, String username) {
        this.id        = id;
        this.title     = title;
        this.lastName  = lastName.toUpperCase();
        this.firstName = firstName;
        this.username  = username.toLowerCase();
    }

    public String getId()        { return id; }
    public String getUsername()  { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }

    /** e.g. "Dr. KAMDEM" */
    public String getFullName() {
        return title + " " + lastName + " " + firstName;
    }

    public void displayInfo() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_RED, "ADMIN PROFILE");
        Colors.thickDivider();
        Colors.printField("Admin ID ", id);
        Colors.printField("Full Name", getFullName());
        Colors.printField("Username ", username);
        Colors.printField("Role     ", "System Administrator");
        Colors.thickDivider();
    }
}
