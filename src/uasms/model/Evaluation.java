package uasms.model;

import java.io.Serializable;

/**
 * Evaluation.java
 * ─────────────────────────────────────────────────────
 * Stores one student's rating + comment for a lecturer
 * in a specific course. One evaluation per student per course.
 * ─────────────────────────────────────────────────────
 */
public class Evaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Fields ────────────────────────────────────────
    private String studentId;
    private String lecturerId;
    private String courseId;
    private int    rating;   // 1 (Poor) → 5 (Excellent)
    private String comment;  // Optional feedback

    // ── Constructor ───────────────────────────────────
    public Evaluation(String studentId, String lecturerId,
                      String courseId, int rating, String comment) {
        this.studentId  = studentId;
        this.lecturerId = lecturerId;
        this.courseId   = courseId;
        this.rating     = rating;
        this.comment    = comment;
    }

    // ── Getters ───────────────────────────────────────
    public String getStudentId()  { return studentId; }
    public String getLecturerId() { return lecturerId; }
    public String getCourseId()   { return courseId; }
    public int    getRating()     { return rating; }
    public String getComment()    { return comment; }

    /**
     * Visual star string: "★★★☆☆" for rating = 3
     */
    public String getStars() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) sb.append(i <= rating ? "★" : "☆");
        return sb.toString();
    }
}
