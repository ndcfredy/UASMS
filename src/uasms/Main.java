package uasms;

import uasms.model.*;
import uasms.module.*;
import uasms.util.*;

/**
 * Main.java  — UASMS v1.0
 * ─────────────────────────────────────────────────────
 * Entry point. Flow:
 *   1. Load HashMaps from .dat files (seed on first run)
 *   2. Splash screen
 *   3. Login menu (Student / Admin / Forgot pwd / About / Exit)
 *   4. On exit → save all data to files
 * ─────────────────────────────────────────────────────
 */
public class Main {

    private static final int MAX_ATTEMPTS = 3;

    public static void main(String[] args) {
        // Enable UTF-8 output for Windows terminals
        System.setOut(new java.io.PrintStream(
                System.out, true, java.nio.charset.StandardCharsets.UTF_8));

        DataStore.loadAll();
        showSplash();
        runLoginLoop();
        DataStore.saveAll();
        InputHelper.close();
    }

    // ═════════════════════════════════════════════════
    //  SPLASH SCREEN
    // ═════════════════════════════════════════════════

    private static void showSplash() {
        System.out.println("\n");
        Colors.starDivider();
        Colors.printCenter(Colors.BOLD_BLUE,   "  ██╗   ██╗ █████╗ ███████╗███╗   ███╗███████╗");
        Colors.printCenter(Colors.BOLD_CYAN,   "  ██║   ██║██╔══██╗██╔════╝████╗ ████║██╔════╝");
        Colors.printCenter(Colors.BOLD_BLUE,   "  ██║   ██║███████║███████╗██╔████╔██║███████╗");
        Colors.printCenter(Colors.BOLD_CYAN,   "  ╚██╗ ██╔╝██╔══██║╚════██║██║╚██╔╝██║╚════██║");
        Colors.printCenter(Colors.BOLD_BLUE,   "   ╚████╔╝ ██║  ██║███████║██║ ╚═╝ ██║███████║");
        Colors.printCenter(Colors.CYAN,        "    ╚═══╝  ╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝╚══════╝");
        System.out.println();
        Colors.printCenter(Colors.BOLD_YELLOW, "PKFokam Institute of Excellence");
        Colors.printCenter(Colors.WHITE,       "Academic Student Management System  v1.0");
        Colors.printCenter(Colors.CYAN,        "HashMap + File Persistence | 2026");
        System.out.println();
        Colors.starDivider();
        System.out.println();
    }

    // ═════════════════════════════════════════════════
    //  LOGIN LOOP
    // ═════════════════════════════════════════════════

    private static void runLoginLoop() {
        int failedAttempts = 0;

        while (true) {
            System.out.println();
            Colors.thickDivider();
            Colors.printCenter(Colors.BOLD_CYAN, "WELCOME  --  PLEASE LOGIN");
            Colors.thickDivider();
            Colors.printMenuItem(Colors.GREEN,   "1", "Student Login");
            Colors.printMenuItem(Colors.RED,     "2", "Admin Login");
            Colors.printMenuItem(Colors.YELLOW,  "3", "Forgot Password");
            Colors.printMenuItem(Colors.MAGENTA, "4", "About This Program");
            Colors.printMenuItem(Colors.WHITE,   "5", "Exit");
            Colors.thickDivider();

            int choice = InputHelper.readInt("  Choice: ", 1, 5);

            switch (choice) {
                case 1 -> {
                    if (failedAttempts >= MAX_ATTEMPTS) {
                        Colors.error("Too many failed attempts. Please restart the program.");
                        return;
                    }
                    Student s = studentLogin();
                    if (s != null) {
                        failedAttempts = 0;
                        runStudentMenu(s);
                    } else {
                        failedAttempts++;
                        int left = MAX_ATTEMPTS - failedAttempts;
                        if (left > 0) Colors.warn(left + " login attempt(s) remaining.");
                        else {
                            Colors.error("Too many failed attempts. Exiting.");
                            return;
                        }
                    }
                }
                case 2 -> {
                    Admin a = adminLogin();
                    if (a != null) new AdminModule(a).showMenu();
                }
                case 3 -> forgotPassword();
                case 4 -> showAbout();
                case 5 -> { showGoodbye(); return; }
            }
        }
    }

    // ═════════════════════════════════════════════════
    //  STUDENT LOGIN
    // ═════════════════════════════════════════════════

    private static Student studentLogin() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_GREEN, "STUDENT LOGIN");
        Colors.divider();
        Colors.printCenter(Colors.CYAN, "Username is not case-sensitive");
        Colors.divider();

        String username = InputHelper.readString("  Username : ");
        String password = InputHelper.readPassword("  Password : ");

        Student student = DataStore.findStudentByUsername(username);
        if (student == null) { Colors.error("Username not found."); return null; }

        String stored = DataStore.credentials.get(student.getUsername());
        if (stored != null && stored.equals(password)) {
            // Welcome uses: LASTNAME Firstname format
            Colors.success("Welcome, " + student.getLastName()
                    + " " + student.getFirstName() + "!");
            return student;
        }
        Colors.error("Incorrect password.");
        return null;
    }

    // ═════════════════════════════════════════════════
    //  ADMIN LOGIN
    // ═════════════════════════════════════════════════

    private static Admin adminLogin() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_RED, "ADMIN LOGIN");
        Colors.divider();
        Colors.printCenter(Colors.CYAN, "admin1 / Admin1@2024  |  admin2 / Admin2@2024");
        Colors.divider();

        String username = InputHelper.readString("  Admin Username : ");
        String password = InputHelper.readPassword("  Password       : ");

        Admin admin = DataStore.findAdminByUsername(username);
        if (admin == null) { Colors.error("Admin not found."); return null; }

        String stored = DataStore.adminPasswords.get(username.toLowerCase());
        if (stored != null && stored.equals(password)) {
            Colors.success("Welcome, " + admin.getFullName() + "!");
            return admin;
        }
        Colors.error("Incorrect admin password.");
        return null;
    }

    // ═════════════════════════════════════════════════
    //  FORGOT PASSWORD
    // ═════════════════════════════════════════════════

    private static void forgotPassword() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_YELLOW, "FORGOT PASSWORD");
        Colors.thickDivider();

        String username = InputHelper.readString("  Enter your username: ");
        Student student = DataStore.findStudentByUsername(username);

        if (student == null) { Colors.error("Username not found."); return; }

        if (!student.hasSecurityAnswer()) {
            Colors.warn("No security question set for this account.");
            Colors.info("Please contact an admin to reset your password.");
            return;
        }

        Colors.divider();
        Colors.printCenter(Colors.BOLD_WHITE,
                "Security Question: " + student.getSecurityQuestion());
        Colors.divider();

        for (int attempt = 1; attempt <= 3; attempt++) {
            String answer = InputHelper.readString("  Your answer: ");
            if (student.checkSecurityAnswer(answer)) {
                Colors.success("Correct answer!");
                Colors.divider();
                String newPwd = InputHelper.readNewPassword("  New password (min 8 chars): ");
                DataStore.credentials.put(student.getUsername(), newPwd);
                DataStore.saveAll();
                Colors.success("Password reset successfully!");
                return;
            } else {
                Colors.error("Wrong answer. " + (3 - attempt) + " attempt(s) left.");
            }
        }
        Colors.error("Too many wrong answers. Contact an admin.");
    }

    // ═════════════════════════════════════════════════
    //  STUDENT MAIN MENU
    // ═════════════════════════════════════════════════

    private static void runStudentMenu(Student student) {
        boolean running = true;
        while (running) {
            System.out.println();
            Colors.thickDivider();
            Colors.printCenter(Colors.BOLD_BLUE, "UASMS  --  MAIN MENU");
            Colors.printCenter(Colors.CYAN,
                    student.getLastName() + " " + student.getFirstName()
                    + "  |  " + student.getMajor()
                    + "  |  Sem." + student.getSemester());
            Colors.thickDivider();
            Colors.printMenuItem(Colors.GREEN,   "1", "Course Registration");
            Colors.printMenuItem(Colors.YELLOW,  "2", "Attendance Management");
            Colors.printMenuItem(Colors.MAGENTA, "3", "Lecturer Evaluation");
            Colors.printMenuItem(Colors.CYAN,    "4", "My Profile");
            Colors.printMenuItem(Colors.BLUE,    "5", "Change My Password");
            Colors.printMenuItem(Colors.BLUE,    "6", "Set Security Question");
            Colors.printMenuItem(Colors.RED,     "7", "Logout");
            Colors.thickDivider();

            switch (InputHelper.readInt("  Choice: ", 1, 7)) {
                case 1 -> new CourseRegistrationModule(student).showMenu();
                case 2 -> new AttendanceManagementModule(student).showMenu();
                case 3 -> new LecturerEvaluationModule(student).showMenu();
                case 4 -> student.displayInfo();
                case 5 -> changePassword(student);
                case 6 -> setSecurityQuestion(student);
                case 7 -> running = false;
            }
        }
    }

    // ═════════════════════════════════════════════════
    //  CHANGE OWN PASSWORD
    // ═════════════════════════════════════════════════

    private static void changePassword(Student student) {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_BLUE, "CHANGE MY PASSWORD");
        Colors.thickDivider();

        String current = InputHelper.readPassword("  Current password      : ");
        String stored  = DataStore.credentials.get(student.getUsername());

        if (stored == null || !stored.equals(current)) {
            Colors.error("Incorrect current password.");
            return;
        }
        String newPwd = InputHelper.readNewPassword("  New password (min 8): ");
        DataStore.credentials.put(student.getUsername(), newPwd);
        DataStore.saveAll();
        Colors.success("Password changed successfully!");
    }

    // ═════════════════════════════════════════════════
    //  SET SECURITY QUESTION
    // ═════════════════════════════════════════════════

    private static void setSecurityQuestion(Student student) {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_BLUE, "SET SECURITY QUESTION");
        Colors.thickDivider();
        Colors.printCenter(Colors.WHITE, "Choose a security question:");
        Colors.divider();

        String[] questions = {
            "What is your mother's maiden name?",
            "What was the name of your first pet?",
            "What city were you born in?",
            "What is your favorite teacher's name?",
            "What was your childhood nickname?"
        };
        for (int i = 0; i < questions.length; i++)
            Colors.printMenuItem(Colors.CYAN, String.valueOf(i + 1), questions[i]);
        Colors.divider();

        int choice = InputHelper.readInt("  Select question: ", 1, 5);
        student.setSecurityQuestion(questions[choice - 1]);
        String answer = InputHelper.readString("  Your answer       : ");
        student.setSecurityAnswer(answer);
        DataStore.saveAll();
        Colors.success("Security question saved!");
        Colors.info("You can now use 'Forgot Password' if needed.");
    }

    // ═════════════════════════════════════════════════
    //  ABOUT PAGE
    // ═════════════════════════════════════════════════

    private static void showAbout() {
        System.out.println();
        Colors.starDivider();
        Colors.printCenter(Colors.BOLD_CYAN,    "ABOUT THIS PROGRAM");
        Colors.starDivider();
        System.out.println();
        Colors.printCenter(Colors.BOLD_YELLOW,  "UASMS");
        Colors.printCenter(Colors.WHITE,        "University Academic Student Management System");
        Colors.printCenter(Colors.WHITE,        "Version 4.0  |  2026");
        System.out.println();
        Colors.divider();
        Colors.printCenter(Colors.BOLD_WHITE,   "Institution");
        Colors.divider();
        Colors.printCenter(Colors.YELLOW,       "PKFokam Institute of Excellence");
        Colors.printCenter(Colors.WHITE,        "Department of Computer Engineering & Technology");
        System.out.println();
        Colors.divider();
        Colors.printCenter(Colors.BOLD_WHITE,   "Course");
        Colors.divider();
        Colors.printCenter(Colors.GREEN,        "Programming & Problem Solving 1 (Java)");
        Colors.printCenter(Colors.WHITE,        "End of Semester Project  |  Semester 2  |  2026");
        System.out.println();
        Colors.divider();
        Colors.printCenter(Colors.BOLD_WHITE,   "Developed by");
        Colors.divider();
        Colors.printCenter(Colors.BOLD_CYAN,    "NGASSA  Fredy");
        Colors.printCenter(Colors.BOLD_CYAN,    "MAFOUO  Brayan");
        Colors.printCenter(Colors.CYAN,         "GitHub: github.com/YourUsername/UASMS");
        System.out.println();
        Colors.divider();
        Colors.printCenter(Colors.BOLD_WHITE,   "Description");
        Colors.divider();
        Colors.printCenter(Colors.WHITE,        "UASMS is a console-based Java application");
        Colors.printCenter(Colors.WHITE,        "that manages university academic activities.");
        Colors.printCenter(Colors.WHITE,        "It covers course registration, attendance");
        Colors.printCenter(Colors.WHITE,        "tracking, and lecturer evaluation.");
        Colors.printCenter(Colors.WHITE,        "Data is stored persistently using HashMaps");
        Colors.printCenter(Colors.WHITE,        "and Java file serialization (.dat files).");
        System.out.println();
        Colors.divider();
        Colors.printCenter(Colors.BOLD_WHITE,   "Technologies Used");
        Colors.divider();
        Colors.printCenter(Colors.GREEN,        "Java 17+  |  OOP  |  HashMap  |  File I/O");
        Colors.printCenter(Colors.GREEN,        "ANSI Colors  |  Java Serialization");
        Colors.printCenter(Colors.CYAN,         "No external libraries — 100% pure Java");
        System.out.println();
        Colors.starDivider();

        InputHelper.readOptional("  Press Enter to go back...");
    }

    // ═════════════════════════════════════════════════
    //  GOODBYE
    // ═════════════════════════════════════════════════

    private static void showGoodbye() {
        System.out.println();
        Colors.starDivider();
        Colors.printCenter(Colors.BOLD_CYAN,   "Thank you for using UASMS!");
        Colors.printCenter(Colors.YELLOW,      "PKFokam Institute of Excellence");
        Colors.printCenter(Colors.WHITE,       "All data saved. Goodbye!");
        Colors.starDivider();
        System.out.println();
    }
}
