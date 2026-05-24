package uasms.model;

import uasms.util.Colors;
import java.io.Serializable;

/**
 * Course.java
 * ─────────────────────────────────────────────────────
 * Represents one university course.
 * displayRow() prints a centered, formatted table row.
 * ─────────────────────────────────────────────────────
 */
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private String courseId;
    private String title;
    private int    credits;
    private String lecturerId;
    private String schedule;

    public Course(String courseId, String title, int credits,
                  String lecturerId, String schedule) {
        this.courseId   = courseId;
        this.title      = title;
        this.credits    = credits;
        this.lecturerId = lecturerId;
        this.schedule   = schedule;
    }

    public String getCourseId()   { return courseId; }
    public String getTitle()      { return title; }
    public int    getCredits()    { return credits; }
    public String getLecturerId() { return lecturerId; }
    public String getSchedule()   { return schedule; }

    /**
     * Print one centered table row for this course.
     * Uses safe ASCII characters — works on all terminals.
     * @param rowNum  row number (1, 2, 3 ...)
     * @param marked  true = student is already registered (show [R])
     */
    public void displayRow(int rowNum, boolean marked) {
        String mark  = marked ? Colors.BOLD_GREEN + "[R]" + Colors.RESET : "   ";
        String shortTitle = title.length() > 28 ? title.substring(0, 27) + "." : title;
        // Build the visible part of the line
        String line  = String.format("%-3d %-7s %-28s %2dcr %s",
                rowNum, courseId, shortTitle, credits, schedule);
        int pad = Math.max(0, (Colors.WIDTH - line.length() - 4) / 2);
        System.out.println(" ".repeat(pad)
                + mark + " "
                + Colors.CYAN    + String.format("%-3d", rowNum) + Colors.RESET
                + Colors.BOLD_WHITE + String.format("%-8s", courseId) + Colors.RESET
                + String.format("%-28s ", shortTitle)
                + Colors.YELLOW  + credits + "cr" + Colors.RESET
                + "  " + Colors.GREEN + schedule + Colors.RESET);
    }

    @Override
    public String toString() {
        return courseId + " - " + title + " (" + credits + " cr)";
    }
}
