package uasms.module;

import uasms.model.*;
import uasms.util.*;
import java.util.*;

/**
 * CourseRegistrationModule.java
 * ─────────────────────────────────────────────────────
 * Allows students to register/drop courses.
 * Uses HashMaps from DataStore; persists via FileManager.
 * Credit limit: 24 credits per semester.
 * ─────────────────────────────────────────────────────
 */
public class CourseRegistrationModule {

    private static final int MAX_CREDITS = 24;
    private final Student student;

    public CourseRegistrationModule(Student student) { this.student = student; }

    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            Colors.thickDivider();
            Colors.printCenter(Colors.BOLD_GREEN, "📚  COURSE REGISTRATION");
            Colors.thickDivider();
            Colors.printMenuItem(Colors.GREEN, "1", "View All Available Courses");
            Colors.printMenuItem(Colors.GREEN, "2", "Register for a Course");
            Colors.printMenuItem(Colors.GREEN, "3", "Drop a Course");
            Colors.printMenuItem(Colors.GREEN, "4", "My Registered Courses");
            Colors.printMenuItem(Colors.RED,   "5", "Back to Main Menu");
            Colors.thickDivider();
            switch (InputHelper.readInt("  Choice: ", 1, 5)) {
                case 1 -> viewAvailableCourses();
                case 2 -> registerCourse();
                case 3 -> dropCourse();
                case 4 -> viewRegisteredCourses();
                case 5 -> running = false;
            }
        }
    }

    /** Show all courses; mark registered ones with ✔ */
    public void viewAvailableCourses() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_CYAN, "ALL COURSES THIS SEMESTER");
        Colors.divider();
        int i = 1;
        for (Course c : DataStore.getAllCoursesSorted()) {
            boolean reg = DataStore.isRegistered(student.getId(), c.getCourseId());
            c.displayRow(i++, reg);
        }
        Colors.divider();
        Colors.printCenter(Colors.BOLD_GREEN, "✔ = Already registered");
        Colors.thickDivider();
    }

    /** Register for a course with full validation */
    private void registerCourse() {
        System.out.println();
        Colors.printCenter(Colors.BOLD_GREEN, "REGISTER FOR A COURSE");
        viewAvailableCourses();

        String courseId = InputHelper.readString("  Course ID to register: ").toUpperCase();
        Course course = DataStore.courses.get(courseId);

        if (course == null) {
            Colors.error("Course '" + courseId + "' not found.");
            return;
        }
        if (DataStore.isRegistered(student.getId(), courseId)) {
            Colors.warn("Already registered for: " + course.getTitle());
            return;
        }
        int currentCredits = DataStore.getTotalCredits(student.getId());
        if (currentCredits + course.getCredits() > MAX_CREDITS) {
            Colors.error("Exceeds the " + MAX_CREDITS + "-credit limit.");
            Colors.info("Current: " + currentCredits + " cr  |  Course: "
                    + course.getCredits() + " cr");
            return;
        }

        // Register: update HashMap and persist to file
        DataStore.registrations
                 .computeIfAbsent(student.getId(), k -> new HashSet<>())
                 .add(courseId);
        DataStore.saveAll();
        Colors.success("Registered for: " + course.getTitle());
        Colors.info("Total credits: " + DataStore.getTotalCredits(student.getId())
                + " / " + MAX_CREDITS);
    }

    /** Drop a registered course */
    private void dropCourse() {
        System.out.println();
        Colors.printCenter(Colors.BOLD_RED, "DROP A COURSE");

        Set<String> regIds = DataStore.getRegisteredCourseIds(student.getId());
        if (regIds.isEmpty()) {
            Colors.warn("No registered courses to drop.");
            return;
        }
        viewRegisteredCourses();

        String courseId = InputHelper.readString("  Course ID to drop: ").toUpperCase();
        if (!DataStore.isRegistered(student.getId(), courseId)) {
            Colors.error("You are not registered for '" + courseId + "'.");
            return;
        }
        Course course = DataStore.courses.get(courseId);
        String confirm = InputHelper.readString(
                "  Drop '" + course.getTitle() + "'? (yes/no): ").toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            DataStore.registrations.get(student.getId()).remove(courseId);
            DataStore.saveAll();
            Colors.success("Dropped: " + course.getTitle());
        } else {
            Colors.info("Drop cancelled.");
        }
    }

    /** List all courses the student is registered in + credit total */
    public void viewRegisteredCourses() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_GREEN, "MY REGISTERED COURSES");
        Colors.divider();

        Set<String> regIds = DataStore.getRegisteredCourseIds(student.getId());
        if (regIds.isEmpty()) {
            Colors.warn("No registered courses yet.");
            Colors.thickDivider();
            return;
        }

        List<String> sorted = new ArrayList<>(regIds);
        Collections.sort(sorted);
        int i = 1;
        for (String cId : sorted) {
            Course c = DataStore.courses.get(cId);
            if (c != null) c.displayRow(i++, false);
        }
        Colors.divider();
        int total = DataStore.getTotalCredits(student.getId());
        Colors.printCenter(Colors.BOLD_YELLOW,
                "Total Credit Hours: " + total + " / " + MAX_CREDITS);
        Colors.thickDivider();
    }
}
