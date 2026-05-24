package uasms.util;

import uasms.model.*;
import java.util.*;

/**
 * DataStore.java
 * ─────────────────────────────────────────────────────
 * Central in-memory store — all data lives in HashMaps.
 * Loaded from .dat files on startup; saved on every change.
 *
 * Name convention everywhere:
 *
 * ─────────────────────────────────────────────────────
 */
public class DataStore {

    // ── HashMaps ──────────────────────────────────────
    public static HashMap<String, Student>               students       = new HashMap<>();
    public static HashMap<String, Lecturer>              lecturers      = new HashMap<>();
    public static HashMap<String, Course>                courses        = new HashMap<>();
    public static HashMap<String, String>                credentials    = new HashMap<>();
    public static HashMap<String, Admin>                 admins         = new HashMap<>();
    public static HashMap<String, String>                adminPasswords = new HashMap<>();
    public static HashMap<String, HashSet<String>>       registrations  = new HashMap<>();
    public static HashMap<String, AttendanceRecord>      attendance     = new HashMap<>();
    public static HashMap<String, ArrayList<Evaluation>> evaluations    = new HashMap<>();

    // ═════════════════════════════════════════════════
    //  LOAD / SAVE
    // ═════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    public static void loadAll() {
        FileManager.initDataFolder();
        if (!FileManager.exists(FileManager.STUDENTS)) {
            Colors.info("First run — loading default data...");
            seedDefaultData();
            saveAll();
            Colors.success("Default data saved.");
        } else {
            students       = FileManager.loadMap(FileManager.STUDENTS);
            credentials    = FileManager.loadMap(FileManager.CREDENTIALS);
            lecturers      = FileManager.loadMap(FileManager.LECTURERS);
            courses        = FileManager.loadMap(FileManager.COURSES);
            admins         = FileManager.loadMap(FileManager.ADMINS);
            adminPasswords = FileManager.loadMap(FileManager.ADMIN_PASSWORDS);
            registrations  = FileManager.loadMap(FileManager.REGISTRATIONS);
            attendance     = FileManager.loadMap(FileManager.ATTENDANCE);
            evaluations    = FileManager.loadMap(FileManager.EVALUATIONS);
            Colors.success("Data loaded successfully.");
        }
    }

    public static void saveAll() {
        FileManager.saveMap(FileManager.STUDENTS,        students);
        FileManager.saveMap(FileManager.CREDENTIALS,     credentials);
        FileManager.saveMap(FileManager.LECTURERS,       lecturers);
        FileManager.saveMap(FileManager.COURSES,         courses);
        FileManager.saveMap(FileManager.ADMINS,          admins);
        FileManager.saveMap(FileManager.ADMIN_PASSWORDS, adminPasswords);
        FileManager.saveMap(FileManager.REGISTRATIONS,   registrations);
        FileManager.saveMap(FileManager.ATTENDANCE,      attendance);
        FileManager.saveMap(FileManager.EVALUATIONS,     evaluations);
    }

    // ═════════════════════════════════════════════════
    //  SEED DATA  (lastName first, UPPERCASE)
    // ═════════════════════════════════════════════════

    private static void seedDefaultData() {
        // Admins
        admins.put("admin1", new Admin("ADM001", "Dr.",   "Admin",  "One",   "admin1"));
        admins.put("admin2", new Admin("ADM002", "Miss",  "Admin",  "Two",   "admin2"));
        adminPasswords.put("admin1", "Admin1@demo");
        adminPasswords.put("admin2", "Admin2@demo");

        // Lecturers: (id, title, LASTNAME, firstname, email, age, dept, courseId)
        putLecturer("L001","Dr.", "SMITH",   "James",   "lecturer1@demo.edu", 45,"Computer Science",    "CS101");
        putLecturer("L002","Dr.", "BROWN",   "Sarah",   "lecturer2@demo.edu", 40,"Computer Science",    "CS102");
        putLecturer("L003","Dr.", "WILSON",  "Mark",    "lecturer3@demo.edu", 38,"Information Systems", "CS103");
        putLecturer("L004","Mr.", "TAYLOR",  "Paul",    "lecturer4@demo.edu", 35,"Languages",           "ENG101");
        putLecturer("L005","Dr.", "JOHNSON", "Emma",    "lecturer5@demo.edu", 42,"Mathematics",         "MATH101");
        putLecturer("L006","Mr.", "DAVIS",   "Luke",    "lecturer6@demo.edu", 37,"Electronics",         "ELE101");
        // Courses: (courseId, title, credits, lecturerId, schedule)
        putCourse("CS101",  "Programming & Problem Solving 1 (Java)", 3,"L001","Mon/Wed 08:00");
        putCourse("CS101L", "Java Lab",                               1,"L001","Fri 10:00");
        putCourse("CS102",  "Python Programming",                     3,"L002","Tue/Thu 08:00");
        putCourse("CS103",  "Web Design & Web Development",           3,"L003","Mon/Wed 10:00");
        putCourse("ENG101", "English Language (Comp 1 & 2)",          3,"L004","Tue/Thu 10:00");
        putCourse("ENG101L","English Lab",                            1,"L004","Sat 08:00");
        putCourse("MATH101","Calculus",                               4,"L005","Mon/Wed/Fri 12:00");
        putCourse("ELE101", "Basic Electronics",                      3,"L006","Tue/Thu 14:00");
        putCourse("CS104",  "C Programming",                          3,"L001","Fri 14:00");

        // Students: (id, LASTNAME, firstname, email, age, username, major, semester)
        putStudent("STU001","DOE",        "John",       "student1@demo.edu", 19,"student1", "Computer Engineering & Technology",2);
        putStudent("STU002","SMITH",      "Emma",       "student2@demo.edu", 18,"student2", "Mechatronics",                     2);
        putStudent("STU003","BROWN",      "Sara",       "student3@demo.edu", 19,"student3", "Computer Engineering & Technology",2);
        putStudent("STU004","WILSON",     "James",      "student4@demo.edu", 20,"student4", "Mechatronics",                     3);
        putStudent("STU005","TAYLOR",     "Alice",      "student5@demo.edu", 18,"student5", "Computer Engineering & Technology",2);
        putStudent("STU006","MARTIN",     "Laura",      "student6@demo.edu", 19,"student6", "Computer Engineering & Technology",2);
        putStudent("STU007","CLARK",      "Sophie",     "student7@demo.edu", 18,"student7", "Computer Engineering & Technology",2);
        putStudent("STU008","WHITE",      "Daniel",     "student8@demo.edu", 20,"student8", "Economic & Management",            4);
        // Credentials
        credentials.put("student1", "Student1@demo");
        credentials.put("student2", "Student2@demo");
        credentials.put("student3", "Student3@demo");
        credentials.put("student4", "Student4@demo");
        credentials.put("student5", "Student5@demo");
        credentials.put("student6", "Student6@demo");
        credentials.put("student7", "Student7@demo");
        credentials.put("student8", "Student8@demo");

        // Empty registration sets for each student
        for (Student s : students.values()) {
            registrations.put(s.getId(), new HashSet<>());
        }
    }

    // ── Private seed helpers ──────────────────────────

    private static void putStudent(String id, String lastName, String firstName,
                                   String email, int age, String username,
                                   String major, int semester) {
        students.put(id, new Student(id, lastName, firstName, email,
                                     age, username, major, semester));
    }

    private static void putLecturer(String id, String title, String lastName,
                                    String firstName, String email, int age,
                                    String department, String courseId) {
        lecturers.put(id, new Lecturer(id, title, lastName, firstName,
                                       email, age, department, courseId));
    }

    private static void putCourse(String courseId, String title, int credits,
                                  String lecturerId, String schedule) {
        courses.put(courseId, new Course(courseId, title, credits, lecturerId, schedule));
    }

    // ═════════════════════════════════════════════════
    //  AUTO-ID GENERATORS
    // ═════════════════════════════════════════════════

    /**
     * Generate the next student ID: STU001, STU002, etc.
     * Scans existing IDs and returns max+1.
     */
    public static String nextStudentId() {
        int max = 0;
        for (String id : students.keySet()) {
            try { max = Math.max(max, Integer.parseInt(id.replace("STU", ""))); }
            catch (NumberFormatException ignored) {}
        }
        return String.format("STU%03d", max + 1);
    }

    /** Generate the next lecturer ID: L001, L002, etc. */
    public static String nextLecturerId() {
        int max = 0;
        for (String id : lecturers.keySet()) {
            try { max = Math.max(max, Integer.parseInt(id.replace("L", ""))); }
            catch (NumberFormatException ignored) {}
        }
        return String.format("L%03d", max + 1);
    }

    // ═════════════════════════════════════════════════
    //  LOOKUP HELPERS
    // ═════════════════════════════════════════════════

    /** Find student by username — case-insensitive */
    public static Student findStudentByUsername(String username) {
        String lower = username.toLowerCase();
        for (Student s : students.values())
            if (s.getUsername().equals(lower)) return s;
        return null;
    }

    /** Find admin by username — case-insensitive */
    public static Admin findAdminByUsername(String username) {
        return admins.get(username.toLowerCase());
    }

    /** Course IDs a student is registered in */
    public static HashSet<String> getRegisteredCourseIds(String studentId) {
        return registrations.getOrDefault(studentId, new HashSet<>());
    }

    public static boolean isRegistered(String studentId, String courseId) {
        return getRegisteredCourseIds(studentId).contains(courseId);
    }

    public static int getTotalCredits(String studentId) {
        int total = 0;
        for (String cId : getRegisteredCourseIds(studentId)) {
            Course c = courses.get(cId);
            if (c != null) total += c.getCredits();
        }
        return total;
    }

    public static AttendanceRecord getOrCreateAttendance(String sid, String cid) {
        String key = sid + "_" + cid;
        attendance.computeIfAbsent(key, k -> new AttendanceRecord(sid, cid));
        return attendance.get(key);
    }

    public static ArrayList<Evaluation> getOrCreateEvalList(String lid, String cid) {
        String key = lid + "_" + cid;
        evaluations.computeIfAbsent(key, k -> new ArrayList<>());
        return evaluations.get(key);
    }

    public static boolean hasEvaluated(String sid, String lid, String cid) {
        for (Evaluation e : getOrCreateEvalList(lid, cid))
            if (e.getStudentId().equals(sid)) return true;
        return false;
    }

    public static List<Course> getAllCoursesSorted() {
        List<Course> list = new ArrayList<>(courses.values());
        list.sort(Comparator.comparing(Course::getCourseId));
        return list;
    }

    public static List<Student> getAllStudentsSorted() {
        List<Student> list = new ArrayList<>(students.values());
        list.sort(Comparator.comparing(Student::getId));
        return list;
    }

    public static List<Lecturer> getAllLecturersSorted() {
        List<Lecturer> list = new ArrayList<>(lecturers.values());
        list.sort(Comparator.comparing(Lecturer::getId));
        return list;
    }

    /**
     * Completely remove a student and all their associated data.
     * (registrations, attendance records, evaluations they submitted)
     */
    public static void removeStudent(String studentId) {
        Student s = students.remove(studentId);
        if (s != null) {
            credentials.remove(s.getUsername());
            registrations.remove(studentId);
            // Remove attendance records for this student
            attendance.keySet().removeIf(k -> k.startsWith(studentId + "_"));
            // Remove evaluations submitted by this student
            for (ArrayList<Evaluation> eList : evaluations.values())
                eList.removeIf(e -> e.getStudentId().equals(studentId));
        }
    }

    /**
     * Remove a lecturer and unlink them from their courses.
     */
    public static void removeLecturer(String lecturerId) {
        lecturers.remove(lecturerId);
        // Remove evaluations for this lecturer
        evaluations.keySet().removeIf(k -> k.startsWith(lecturerId + "_"));
    }

    /**
     * Remove a course, drop all student registrations for it,
     * and remove its attendance and evaluation data.
     */
    public static void removeCourse(String courseId) {
        courses.remove(courseId);
        // Drop all student registrations for this course
        for (HashSet<String> set : registrations.values()) set.remove(courseId);
        // Remove attendance records for this course
        attendance.keySet().removeIf(k -> k.endsWith("_" + courseId));
        // Remove evaluations for this course
        evaluations.keySet().removeIf(k -> k.endsWith("_" + courseId));
    }
}
