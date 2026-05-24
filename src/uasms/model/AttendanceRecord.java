package uasms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * AttendanceRecord.java
 * ─────────────────────────────────────────────────────
 * Tracks attendance for ONE student in ONE course.
 * Each element in the list = one class session.
 * true  = Present | false = Absent
 * ─────────────────────────────────────────────────────
 */
public class AttendanceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    // Minimum attendance % to avoid a warning
    public static final double THRESHOLD = 75.0;

    // ── Fields ────────────────────────────────────────
    private String        studentId;
    private String        courseId;
    private List<Boolean> sessions;   // Each element = one class session

    // ── Constructor ───────────────────────────────────
    public AttendanceRecord(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId  = courseId;
        this.sessions  = new ArrayList<>();
    }

    // ── Getters ───────────────────────────────────────
    public String getStudentId() { return studentId; }
    public String getCourseId()  { return courseId; }

    /** Add one session result */
    public void addSession(boolean present) { sessions.add(present); }

    public int getTotalSessions() { return sessions.size(); }

    public int getPresentCount() {
        int c = 0;
        for (boolean s : sessions) if (s) c++;
        return c;
    }

    public int getAbsentCount() { return getTotalSessions() - getPresentCount(); }

    /** Attendance % — returns 0.0 if no sessions recorded yet */
    public double getPercentage() {
        if (sessions.isEmpty()) return 0.0;
        return (getPresentCount() * 100.0) / getTotalSessions();
    }

    /** True if the student is below the 75% threshold */
    public boolean isBelowThreshold() { return getPercentage() < THRESHOLD; }
}
