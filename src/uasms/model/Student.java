package uasms.model;

import uasms.util.Colors;
import java.io.Serializable;

/**
 * Student.java
 * ─────────────────────────────────────────────────────
 * Represents a university student.
 * Extends Person (lastName UPPERCASE, firstName normal).
 * Includes security question for forgot-password flow.
 * ─────────────────────────────────────────────────────
 */
public class Student extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String major;
    private int    semester;
    private String securityQuestion;
    private String securityAnswer;   // stored lowercase

    // Constructor: (id, lastName, firstName, email, age, username, major, semester)
    public Student(String id, String lastName, String firstName,
                   String email, int age, String username,
                   String major, int semester) {
        super(id, lastName, firstName, email, age);
        this.username         = username.toLowerCase();
        this.major            = major;
        this.semester         = semester;
        this.securityQuestion = "";
        this.securityAnswer   = "";
    }

    // ── Getters ───────────────────────────────────────
    public String getUsername()         { return username; }
    public String getMajor()            { return major; }
    public int    getSemester()         { return semester; }
    public String getSecurityQuestion() { return securityQuestion; }

    // ── Setters ───────────────────────────────────────
    public void setMajor(String major)        { this.major    = major; }
    public void setSemester(int semester)     { this.semester = semester; }
    public void setSecurityQuestion(String q) { this.securityQuestion = q; }
    public void setSecurityAnswer(String a)   { this.securityAnswer   = a.toLowerCase(); }

    public boolean checkSecurityAnswer(String input) {
        return this.securityAnswer.equalsIgnoreCase(input.trim());
    }

    public boolean hasSecurityAnswer() {
        return securityAnswer != null && !securityAnswer.isEmpty();
    }

    /**
     * Display a professional centered student profile card.
     * Welcome line uses "LASTNAME Firstname" format.
     */
    @Override
    public void displayInfo() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_CYAN, "STUDENT PROFILE");
        Colors.thickDivider();
        Colors.printField("Student ID", id);
        Colors.printField("Full Name ", getFullName());      // "NGASSA Fredy"
        Colors.printField("Email     ", email);
        Colors.printField("Age       ", age + " years");
        Colors.printField("Major     ", major);
        Colors.printField("Semester  ", "Semester " + semester);
        Colors.printField("Username  ", username);
        Colors.thickDivider();
    }

    @Override
    public String toString() {
        return id + " | " + getFullName() + " | " + major + " Sem." + semester;
    }
}
