package uasms.model;

import java.io.Serializable;

/**
 * Person.java
 * ─────────────────────────────────────────────────────
 * Abstract base class for Student and Lecturer.
 * Naming convention:
 *   lastName  → stored and displayed in UPPERCASE
 *               e.g. "NGASSA", "KAZE", "ALIOU"
 *   firstName → normal case  e.g. "Fredy", "Brayan"
 *
 * getFullName() returns "LASTNAME Firstname"
 *   e.g. "NGASSA Fredy"
 * ─────────────────────────────────────────────────────
 */
public abstract class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String id;
    protected String lastName;    // Always UPPERCASE  e.g. "NGASSA"
    protected String firstName;   // Normal case       e.g. "Fredy"
    protected String email;
    protected int    age;

    public Person(String id, String lastName, String firstName,
                  String email, int age) {
        this.id        = id;
        this.lastName  = lastName.toUpperCase();   // last name always uppercase
        this.firstName = capitalize(firstName);    // first name capitalised
        this.email     = email;
        this.age       = age;
    }

    // ── Getters ───────────────────────────────────────
    public String getId()        { return id; }
    public String getLastName()  { return lastName; }
    public String getFirstName() { return firstName; }
    public String getEmail()     { return email; }
    public int    getAge()       { return age; }

    /**
     * Display name format: "LASTNAME Firstname"
     * e.g. "NGASSA Fredy", "KAZE Brayan"
     */
    public String getFullName() {
        return lastName + " " + firstName;
    }

    // ── Setters ───────────────────────────────────────
    public void setLastName(String lastName)   { this.lastName  = lastName.toUpperCase(); }
    public void setFirstName(String firstName) { this.firstName = capitalize(firstName); }
    public void setEmail(String email)         { this.email     = email; }
    public void setAge(int age)                { this.age       = age; }

    /** Capitalize first letter, rest lowercase. e.g. "fredy" -> "Fredy" */
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public abstract void displayInfo();
}
