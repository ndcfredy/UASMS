package uasms.model;

import uasms.util.Colors;
import java.io.Serializable;

/**
 * Lecturer.java
 * ─────────────────────────────────────────────────────
 * Represents a university lecturer.
 * lastName stored UPPERCASE, firstName normal case.
 * ─────────────────────────────────────────────────────
 */
public class Lecturer extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;       // "Dr." or "Mr." or "Miss"
    private String department;
    private String courseId;    // Primary course ID

    // Constructor: (id, title, lastName, firstName, email, age, department, courseId)
    public Lecturer(String id, String title, String lastName, String firstName,
                    String email, int age, String department, String courseId) {
        super(id, lastName, firstName, email, age);
        this.title      = title;
        this.department = department;
        this.courseId   = courseId;
    }

    public String getTitle()      { return title; }
    public String getDepartment() { return department; }
    public String getCourseId()   { return courseId; }

    /** e.g. "Dr. MOYOU Leonel" */
    public String getTitledName() {
        return title + " " + lastName + " " + firstName;
    }

    @Override
    public void displayInfo() {
        Colors.printField("Lecturer  ", getTitledName());
        Colors.printField("Department", department);
    }

    @Override
    public String toString() {
        return id + " | " + getTitledName() + " | " + department;
    }
}
