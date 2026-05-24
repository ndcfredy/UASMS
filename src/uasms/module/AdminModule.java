package uasms.module;

import uasms.model.*;
import uasms.util.*;
import java.util.*;

/**
 * AdminModule.java
 * ─────────────────────────────────────────────────────
 * Full admin control panel. Admins can:
 *   - Add / remove students, lecturers, courses
 *   - Edit any student's information
 *   - Reset any student's password
 *   - View all data (students, lecturers, courses)
 * ─────────────────────────────────────────────────────
 */
public class AdminModule {

    private final Admin admin;

    public AdminModule(Admin admin) { this.admin = admin; }

    // ── Main admin menu ───────────────────────────────
    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            Colors.thickDivider();
            Colors.printCenter(Colors.BOLD_RED, "ADMIN PANEL");
            Colors.printCenter(Colors.CYAN, "Logged in as: " + admin.getFullName());
            Colors.thickDivider();
            Colors.printMenuItem(Colors.BOLD_GREEN,   "1", "Student Management");
            Colors.printMenuItem(Colors.BOLD_MAGENTA, "2", "Lecturer Management");
            Colors.printMenuItem(Colors.BOLD_CYAN,    "3", "Course Management");
            Colors.printMenuItem(Colors.YELLOW,       "4", "View All Data");
            Colors.printMenuItem(Colors.RED,          "5", "Logout");
            Colors.thickDivider();
            switch (InputHelper.readInt("  Choice: ", 1, 5)) {
                case 1 -> studentMenu();
                case 2 -> lecturerMenu();
                case 3 -> courseMenu();
                case 4 -> viewAllData();
                case 5 -> running = false;
            }
        }
    }

    // ═════════════════════════════════════════════════
    //  STUDENT MANAGEMENT
    // ═════════════════════════════════════════════════

    private void studentMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            Colors.thickDivider();
            Colors.printCenter(Colors.BOLD_GREEN, "STUDENT MANAGEMENT");
            Colors.thickDivider();
            Colors.printMenuItem(Colors.GREEN, "1", "View All Students");
            Colors.printMenuItem(Colors.GREEN, "2", "Add New Student");
            Colors.printMenuItem(Colors.GREEN, "3", "Edit Student Info");
            Colors.printMenuItem(Colors.GREEN, "4", "Reset Student Password");
            Colors.printMenuItem(Colors.RED,   "5", "Remove Student");
            Colors.printMenuItem(Colors.YELLOW,"6", "Back");
            Colors.thickDivider();
            switch (InputHelper.readInt("  Choice: ", 1, 6)) {
                case 1 -> viewAllStudents();
                case 2 -> addStudent();
                case 3 -> editStudent();
                case 4 -> resetStudentPassword();
                case 5 -> removeStudent();
                case 6 -> running = false;
            }
        }
    }

    /** Show all students in a formatted centered table */
    private void viewAllStudents() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_GREEN, "ALL STUDENTS");
        Colors.divider();
        List<Student> list = DataStore.getAllStudentsSorted();
        if (list.isEmpty()) { Colors.warn("No students registered."); Colors.thickDivider(); return; }
        for (Student s : list) {
            Colors.printCenter(Colors.CYAN, s.getId() + "  |  " + s.getFullName());
            Colors.printCenter(Colors.WHITE, s.getMajor() + " - Semester " + s.getSemester() + " - Age " + s.getAge());
            Colors.printCenter(Colors.YELLOW, "User: " + s.getUsername() + "  |  " + s.getEmail());
            Colors.divider();
        }
        Colors.thickDivider();
    }

    /**
     * Admin adds a new student.
     * Admin fills in all fields and sets the password directly.
     * ID is auto-generated (STU009, STU010, etc.)
     */
    private void addStudent() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_GREEN, "ADD NEW STUDENT");
        Colors.thickDivider();

        // Auto-generate next ID
        String id = DataStore.nextStudentId();
        Colors.printCenter(Colors.CYAN, "Auto-generated ID: " + id);
        Colors.divider();

        String lastName  = InputHelper.readString("  Last Name  (e.g. NGASSA)  : ").toUpperCase();
        String firstName = InputHelper.readString("  First Name (e.g. Fredy)   : ");
        String email     = InputHelper.readString("  Email                     : ");
        int    age       = InputHelper.readInt   ("  Age                       : ", 15, 60);
        String major     = InputHelper.readString("  Major                     : ");
        int    semester  = InputHelper.readInt   ("  Semester (1-8)            : ", 1, 8);
        String username  = InputHelper.readString("  Username                  : ").toLowerCase();

        // Check username is unique
        if (DataStore.findStudentByUsername(username) != null) {
            Colors.error("Username '" + username + "' is already taken. Student not added.");
            return;
        }

        // Admin sets the password directly
        Colors.info("Set the student's initial password (min 8 chars):");
        String password = InputHelper.readNewPassword("  Password                  : ");

        // Create student and add to HashMaps
        Student s = new Student(id, lastName, firstName, email, age, username, major, semester);
        DataStore.students.put(id, s);
        DataStore.credentials.put(username, password);
        DataStore.registrations.put(id, new HashSet<>());
        DataStore.saveAll();

        Colors.success("Student added: " + s.getFullName() + " [" + id + "]");
        Colors.info("Username: " + username + " | Tell them their password.");
    }

    /**
     * Admin edits a student's fields.
     * Only admin can change student information.
     */
    private void editStudent() {
        System.out.println();
        Colors.printCenter(Colors.BOLD_GREEN, "EDIT STUDENT INFO");
        Colors.divider();

        Student s = pickStudent();
        if (s == null) return;
        s.displayInfo();

        Colors.printCenter(Colors.BOLD_WHITE, "Which field to edit?");
        Colors.printMenuItem(Colors.YELLOW, "1", "Last Name");
        Colors.printMenuItem(Colors.YELLOW, "2", "First Name");
        Colors.printMenuItem(Colors.YELLOW, "3", "Email");
        Colors.printMenuItem(Colors.YELLOW, "4", "Age");
        Colors.printMenuItem(Colors.YELLOW, "5", "Major");
        Colors.printMenuItem(Colors.YELLOW, "6", "Semester");
        Colors.printMenuItem(Colors.RED,    "7", "Cancel");
        Colors.divider();

        switch (InputHelper.readInt("  Choice: ", 1, 7)) {
            case 1 -> { s.setLastName(InputHelper.readString("  New Last Name  : ")); }
            case 2 -> { s.setFirstName(InputHelper.readString("  New First Name : ")); }
            case 3 -> { s.setEmail(InputHelper.readString("  New Email      : ")); }
            case 4 -> { s.setAge(InputHelper.readInt("  New Age        : ", 15, 60)); }
            case 5 -> { s.setMajor(InputHelper.readString("  New Major      : ")); }
            case 6 -> { s.setSemester(InputHelper.readInt("  New Semester   : ", 1, 8)); }
            case 7 -> { Colors.info("Edit cancelled."); return; }
        }
        DataStore.saveAll();
        Colors.success("Student info updated: " + s.getFullName());
    }

    /** Admin resets a student's password — no old password needed */
    private void resetStudentPassword() {
        System.out.println();
        Colors.printCenter(Colors.BOLD_GREEN, "RESET STUDENT PASSWORD");
        Colors.divider();

        Student s = pickStudent();
        if (s == null) return;

        Colors.printCenter(Colors.CYAN, "Resetting password for: " + s.getFullName());
        String newPwd = InputHelper.readNewPassword("  New password (min 8 chars): ");
        DataStore.credentials.put(s.getUsername(), newPwd);
        DataStore.saveAll();
        Colors.success("Password reset for " + s.getFullName());
        Colors.info("Inform them of their new password.");
    }

    /**
     * Hard delete a student.
     * Admin must type "DELETE" to confirm.
     * Removes student + credentials + registrations + attendance data.
     */
    private void removeStudent() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_RED, "REMOVE STUDENT");
        Colors.thickDivider();

        Student s = pickStudent();
        if (s == null) return;

        Colors.printCenter(Colors.BOLD_RED, "WARNING: This will permanently delete:");
        Colors.printCenter(Colors.WHITE, s.getFullName() + " [" + s.getId() + "]");
        Colors.printCenter(Colors.RED, "All their registrations and attendance records.");
        Colors.divider();
        String confirm = InputHelper.readString("  Type DELETE to confirm: ");

        if (confirm.equals("DELETE")) {
            String name = s.getFullName();
            DataStore.removeStudent(s.getId());
            DataStore.saveAll();
            Colors.success("Student permanently removed: " + name);
        } else {
            Colors.info("Deletion cancelled.");
        }
    }

    // ═════════════════════════════════════════════════
    //  LECTURER MANAGEMENT
    // ═════════════════════════════════════════════════

    private void lecturerMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            Colors.thickDivider();
            Colors.printCenter(Colors.BOLD_MAGENTA, "LECTURER MANAGEMENT");
            Colors.thickDivider();
            Colors.printMenuItem(Colors.MAGENTA, "1", "View All Lecturers");
            Colors.printMenuItem(Colors.MAGENTA, "2", "Add New Lecturer");
            Colors.printMenuItem(Colors.RED,     "3", "Remove Lecturer");
            Colors.printMenuItem(Colors.YELLOW,  "4", "Back");
            Colors.thickDivider();
            switch (InputHelper.readInt("  Choice: ", 1, 4)) {
                case 1 -> viewAllLecturers();
                case 2 -> addLecturer();
                case 3 -> removeLecturer();
                case 4 -> running = false;
            }
        }
    }

    private void viewAllLecturers() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_MAGENTA, "ALL LECTURERS");
        Colors.divider();
        List<Lecturer> list = DataStore.getAllLecturersSorted();
        if (list.isEmpty()) { Colors.warn("No lecturers registered."); Colors.thickDivider(); return; }
        for (Lecturer l : list) {
            Colors.printCenter(Colors.MAGENTA, l.getId() + "  |  " + l.getTitledName());
            Colors.printCenter(Colors.WHITE, "Dept: " + l.getDepartment() + "  |  Course: " + l.getCourseId());
            Colors.divider();
        }
        Colors.thickDivider();
    }

    /** Add a new lecturer with auto-generated ID */
    private void addLecturer() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_MAGENTA, "ADD NEW LECTURER");
        Colors.thickDivider();

        String id = DataStore.nextLecturerId();
        Colors.printCenter(Colors.CYAN, "Auto-generated ID: " + id);
        Colors.divider();

        Colors.printCenter(Colors.WHITE, "Title:");
        Colors.printMenuItem(Colors.CYAN, "1", "Dr.");
        Colors.printMenuItem(Colors.CYAN, "2", "Mr.");
        Colors.printMenuItem(Colors.CYAN, "3", "Mrs.");
        Colors.printMenuItem(Colors.CYAN, "4", "Miss");
        Colors.printMenuItem(Colors.CYAN, "5", "Prof.");
        String[] titles = {"Dr.", "Mr.", "Mrs.", "Miss", "Prof."};
        String title = titles[InputHelper.readInt("  Title choice: ", 1, 5) - 1];

        String lastName   = InputHelper.readString("  Last Name   (e.g. MOYOU)   : ").toUpperCase();
        String firstName  = InputHelper.readString("  First Name  (e.g. Leonel)  : ");
        String email      = InputHelper.readString("  Email                      : ");
        int    age        = InputHelper.readInt   ("  Age                        : ", 22, 80);
        String department = InputHelper.readString("  Department                 : ");
        String courseId   = InputHelper.readString("  Primary Course ID          : ").toUpperCase();

        Lecturer l = new Lecturer(id, title, lastName, firstName,
                                  email, age, department, courseId);
        DataStore.lecturers.put(id, l);
        DataStore.saveAll();
        Colors.success("Lecturer added: " + l.getTitledName() + " [" + id + "]");
    }

    /** Hard delete a lecturer — must type DELETE to confirm */
    private void removeLecturer() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_RED, "REMOVE LECTURER");
        Colors.thickDivider();
        viewAllLecturers();

        String id = InputHelper.readString("  Lecturer ID to remove (e.g. L001): ").toUpperCase();
        Lecturer l = DataStore.lecturers.get(id);
        if (l == null) { Colors.error("Lecturer '" + id + "' not found."); return; }

        Colors.printCenter(Colors.BOLD_RED, "WARNING: Permanently remove " + l.getTitledName() + "?");
        Colors.printCenter(Colors.RED, "This also removes all their course evaluations.");
        String confirm = InputHelper.readString("  Type DELETE to confirm: ");

        if (confirm.equals("DELETE")) {
            String name = l.getTitledName();
            DataStore.removeLecturer(id);
            DataStore.saveAll();
            Colors.success("Lecturer removed: " + name);
        } else {
            Colors.info("Deletion cancelled.");
        }
    }

    // ═════════════════════════════════════════════════
    //  COURSE MANAGEMENT
    // ═════════════════════════════════════════════════

    private void courseMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            Colors.thickDivider();
            Colors.printCenter(Colors.BOLD_CYAN, "COURSE MANAGEMENT");
            Colors.thickDivider();
            Colors.printMenuItem(Colors.CYAN,  "1", "View All Courses");
            Colors.printMenuItem(Colors.CYAN,  "2", "Add New Course");
            Colors.printMenuItem(Colors.RED,   "3", "Remove Course");
            Colors.printMenuItem(Colors.YELLOW,"4", "Back");
            Colors.thickDivider();
            switch (InputHelper.readInt("  Choice: ", 1, 4)) {
                case 1 -> viewAllCourses();
                case 2 -> addCourse();
                case 3 -> removeCourse();
                case 4 -> running = false;
            }
        }
    }

    private void viewAllCourses() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_CYAN, "ALL COURSES");
        Colors.divider();
        List<Course> list = DataStore.getAllCoursesSorted();
        if (list.isEmpty()) { Colors.warn("No courses available."); Colors.thickDivider(); return; }
        for (Course c : list) {
            Lecturer l = DataStore.lecturers.get(c.getLecturerId());
            String lName = (l != null) ? l.getTitledName() : "Unassigned";
            Colors.printCenter(Colors.CYAN, c.getCourseId() + "  |  " + c.getTitle());
            Colors.printCenter(Colors.WHITE, "Credits: " + c.getCredits()
                    + "  |  Lecturer: " + lName);
            Colors.printCenter(Colors.GREEN, "Schedule: " + c.getSchedule());
            Colors.divider();
        }
        Colors.thickDivider();
    }

    /** Add a new course — admin provides all details */
    private void addCourse() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_CYAN, "ADD NEW COURSE");
        Colors.thickDivider();

        viewAllLecturers(); // Show lecturers so admin can pick a valid ID

        String courseId    = InputHelper.readString("  Course ID   (e.g. CS105)   : ").toUpperCase();
        if (DataStore.courses.containsKey(courseId)) {
            Colors.error("Course ID '" + courseId + "' already exists.");
            return;
        }
        String title       = InputHelper.readString("  Course Title               : ");
        int    credits     = InputHelper.readInt   ("  Credit Hours (1-6)         : ", 1, 6);
        String lecturerId  = InputHelper.readString("  Lecturer ID  (e.g. L001)   : ").toUpperCase();
        if (!DataStore.lecturers.containsKey(lecturerId)) {
            Colors.error("Lecturer ID '" + lecturerId + "' not found.");
            return;
        }
        String schedule    = InputHelper.readString("  Schedule (e.g. Mon/Wed 08:00): ");

        DataStore.courses.put(courseId, new Course(courseId, title, credits, lecturerId, schedule));
        DataStore.saveAll();
        Colors.success("Course added: " + courseId + " - " + title);
    }

    /** Hard delete a course — must type DELETE to confirm */
    private void removeCourse() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_RED, "REMOVE COURSE");
        Colors.thickDivider();
        viewAllCourses();

        String courseId = InputHelper.readString("  Course ID to remove: ").toUpperCase();
        Course c = DataStore.courses.get(courseId);
        if (c == null) { Colors.error("Course '" + courseId + "' not found."); return; }

        Colors.printCenter(Colors.BOLD_RED, "WARNING: Permanently remove:");
        Colors.printCenter(Colors.WHITE, c.getCourseId() + " - " + c.getTitle());
        Colors.printCenter(Colors.RED, "Drops all student registrations for this course.");
        String confirm = InputHelper.readString("  Type DELETE to confirm: ");

        if (confirm.equals("DELETE")) {
            String title = c.getTitle();
            DataStore.removeCourse(courseId);
            DataStore.saveAll();
            Colors.success("Course removed: " + title);
        } else {
            Colors.info("Deletion cancelled.");
        }
    }

    // ═════════════════════════════════════════════════
    //  VIEW ALL DATA
    // ═════════════════════════════════════════════════

    private void viewAllData() {
        viewAllStudents();
        viewAllLecturers();
        viewAllCourses();
    }

    // ═════════════════════════════════════════════════
    //  HELPER: Pick a student from list
    // ═════════════════════════════════════════════════

    private Student pickStudent() {
        viewAllStudents();
        String id = InputHelper.readString("  Enter Student ID (e.g. STU001): ").toUpperCase();
        Student s = DataStore.students.get(id);
        if (s == null) Colors.error("Student '" + id + "' not found.");
        return s;
    }
}
