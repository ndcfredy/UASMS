package uasms.module;

import uasms.model.*;
import uasms.util.*;
import java.util.*;

/**
 * AttendanceManagementModule.java
 * ─────────────────────────────────────────────────────
 * Tracks attendance per student per course.
 * Records saved in HashMap → persisted to attendance.dat
 * 75% threshold warning included.
 * ─────────────────────────────────────────────────────
 */
public class AttendanceManagementModule {

    private final Student student;

    public AttendanceManagementModule(Student student) { this.student = student; }

    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            Colors.thickDivider();
            Colors.printCenter(Colors.BOLD_YELLOW, "📋  ATTENDANCE MANAGEMENT");
            Colors.thickDivider();
            Colors.printMenuItem(Colors.YELLOW, "1", "Mark Attendance");
            Colors.printMenuItem(Colors.YELLOW, "2", "View Attendance by Course");
            Colors.printMenuItem(Colors.YELLOW, "3", "Full Attendance Report");
            Colors.printMenuItem(Colors.RED,    "4", "Back to Main Menu");
            Colors.thickDivider();
            switch (InputHelper.readInt("  Choice: ", 1, 4)) {
                case 1 -> markAttendance();
                case 2 -> viewByCourse();
                case 3 -> viewFullReport();
                case 4 -> running = false;
            }
        }
    }

    /** Mark one session (Present or Absent) → saved to HashMap + file */
    private void markAttendance() {
        System.out.println();
        Colors.printCenter(Colors.BOLD_YELLOW, "MARK ATTENDANCE");
        Colors.divider();

        List<String> regIds = new ArrayList<>(
                DataStore.getRegisteredCourseIds(student.getId()));
        Collections.sort(regIds);

        if (regIds.isEmpty()) {
            Colors.warn("No registered courses. Please register first.");
            return;
        }

        // List courses to pick from
        for (int i = 0; i < regIds.size(); i++) {
            Course c = DataStore.courses.get(regIds.get(i));
            if (c != null)
                Colors.printCenter(Colors.CYAN, (i + 1) + ".  "
                        + c.getCourseId() + " – " + c.getTitle());
        }
        Colors.divider();

        int idx = InputHelper.readInt("  Select course: ", 1, regIds.size());
        String courseId = regIds.get(idx - 1);
        Course course = DataStore.courses.get(courseId);

        Colors.printCenter(Colors.BOLD_WHITE, "Marking for: " + course.getTitle());
        Colors.printCenter(Colors.GREEN,   "1.  Present");
        Colors.printCenter(Colors.RED,     "2.  Absent");
        boolean present = InputHelper.readInt("  Status: ", 1, 2) == 1;

        // Save session to HashMap then persist
        AttendanceRecord record =
                DataStore.getOrCreateAttendance(student.getId(), courseId);
        record.addSession(present);
        DataStore.saveAll();

        if (present) Colors.success("Marked PRESENT for " + course.getTitle());
        else         Colors.warn("Marked ABSENT for " + course.getTitle());

        // Show warning if below threshold
        if (record.isBelowThreshold()) {
            Colors.error("⚠  Attendance is now "
                    + String.format("%.1f", record.getPercentage())
                    + "% — below the required "
                    + AttendanceRecord.THRESHOLD + "%!");
        }
    }

    /** View attendance stats for a single course */
    private void viewByCourse() {
        System.out.println();
        Colors.printCenter(Colors.BOLD_YELLOW, "ATTENDANCE BY COURSE");
        Colors.divider();

        List<String> regIds = new ArrayList<>(
                DataStore.getRegisteredCourseIds(student.getId()));
        Collections.sort(regIds);

        if (regIds.isEmpty()) { Colors.warn("No registered courses."); return; }

        for (int i = 0; i < regIds.size(); i++) {
            Course c = DataStore.courses.get(regIds.get(i));
            if (c != null)
                Colors.printCenter(Colors.CYAN,
                        (i + 1) + ".  " + c.getCourseId() + " – " + c.getTitle());
        }
        Colors.divider();

        int idx = InputHelper.readInt("  Select course: ", 1, regIds.size());
        String courseId = regIds.get(idx - 1);
        Course course = DataStore.courses.get(courseId);
        AttendanceRecord record =
                DataStore.getOrCreateAttendance(student.getId(), courseId);

        printCourseStats(course, record);
    }

    /** Helper: display attendance stats for one course */
    private void printCourseStats(Course course, AttendanceRecord record) {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_CYAN, course.getTitle());
        Colors.divider();

        if (record.getTotalSessions() == 0) {
            Colors.info("No sessions recorded yet.");
            Colors.thickDivider();
            return;
        }

        Colors.printField("Present  ", record.getPresentCount() + " session(s)");
        Colors.printField("Absent   ", record.getAbsentCount()  + " session(s)");
        Colors.printField("Total    ", record.getTotalSessions() + " session(s)");

        double pct = record.getPercentage();
        String col = pct >= AttendanceRecord.THRESHOLD ? Colors.BOLD_GREEN : Colors.BOLD_RED;
        Colors.printCenter(col, String.format("Attendance: %.1f%%", pct));

        if (record.isBelowThreshold())
             Colors.error("Below required " + AttendanceRecord.THRESHOLD + "%!");
        else Colors.success("Attendance is satisfactory.");
        Colors.thickDivider();
    }

    /** Print a full colored attendance table for all registered courses */
    private void viewFullReport() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_YELLOW, "FULL ATTENDANCE REPORT");
        Colors.divider();
        Colors.printCenter(Colors.BOLD_WHITE,
                String.format("%-8s %-25s %7s %7s %7s %7s",
                        "CODE", "COURSE", "PRESENT", "ABSENT", "TOTAL", "%"));
        Colors.divider();

        List<String> regIds = new ArrayList<>(
                DataStore.getRegisteredCourseIds(student.getId()));
        Collections.sort(regIds);

        if (regIds.isEmpty()) {
            Colors.warn("No registered courses.");
            Colors.thickDivider();
            return;
        }

        for (String cId : regIds) {
            Course c = DataStore.courses.get(cId);
            AttendanceRecord r =
                    DataStore.getOrCreateAttendance(student.getId(), cId);
            double pct = r.getPercentage();
            String pctColor = pct >= AttendanceRecord.THRESHOLD
                    ? Colors.GREEN : Colors.BOLD_RED;
            String warn = r.isBelowThreshold() ? " ⚠" : "";
            String title = c.getTitle().length() > 25
                    ? c.getTitle().substring(0, 24) + "…" : c.getTitle();

            String line = String.format("%-8s %-25s %7d %7d %7d ",
                    cId, title,
                    r.getPresentCount(), r.getAbsentCount(), r.getTotalSessions());
            int pad = Math.max(0, (Colors.WIDTH - line.length() - 10) / 2);
            System.out.println(" ".repeat(pad)
                    + Colors.CYAN + String.format("%-8s", cId) + Colors.RESET
                    + String.format(" %-25s ", title)
                    + String.format("%7d %7d %7d ",
                            r.getPresentCount(), r.getAbsentCount(), r.getTotalSessions())
                    + pctColor + String.format("%6.1f%%", pct) + Colors.RESET
                    + Colors.BOLD_RED + warn + Colors.RESET);
        }
        Colors.divider();
        Colors.printCenter(Colors.BOLD_RED, "⚠ = Below " + (int) AttendanceRecord.THRESHOLD + "% threshold");
        Colors.thickDivider();
    }
}
