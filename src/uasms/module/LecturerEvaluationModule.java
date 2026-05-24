package uasms.module;

import uasms.model.*;
import uasms.util.*;
import java.util.*;

/**
 * LecturerEvaluationModule.java
 * ─────────────────────────────────────────────────────
 * Students rate lecturers (1–5 stars) for courses they
 * are registered in. One evaluation per student per course.
 * Results persisted in HashMap → evaluations.dat
 * ─────────────────────────────────────────────────────
 */
public class LecturerEvaluationModule {

    private final Student student;

    public LecturerEvaluationModule(Student student) { this.student = student; }

    public void showMenu() {
        boolean running = true;
        while (running) {
            System.out.println();
            Colors.thickDivider();
            Colors.printCenter(Colors.BOLD_MAGENTA, "⭐  LECTURER EVALUATION");
            Colors.thickDivider();
            Colors.printMenuItem(Colors.MAGENTA, "1", "Evaluate a Lecturer");
            Colors.printMenuItem(Colors.MAGENTA, "2", "View Course Feedback");
            Colors.printMenuItem(Colors.MAGENTA, "3", "Top-Rated Lecturers");
            Colors.printMenuItem(Colors.RED,     "4", "Back to Main Menu");
            Colors.thickDivider();
            switch (InputHelper.readInt("  Choice: ", 1, 4)) {
                case 1 -> evaluateLecturer();
                case 2 -> viewFeedback();
                case 3 -> viewTopRated();
                case 4 -> running = false;
            }
        }
    }

    /** Submit a star rating + comment — persisted to HashMap + file */
    private void evaluateLecturer() {
        System.out.println();
        Colors.printCenter(Colors.BOLD_MAGENTA, "EVALUATE A LECTURER");
        Colors.divider();

        List<String> regIds = new ArrayList<>(
                DataStore.getRegisteredCourseIds(student.getId()));
        Collections.sort(regIds);

        if (regIds.isEmpty()) {
            Colors.warn("No registered courses to evaluate.");
            return;
        }

        // Show courses with their lecturer names
        for (int i = 0; i < regIds.size(); i++) {
            Course c    = DataStore.courses.get(regIds.get(i));
            Lecturer l  = DataStore.lecturers.get(c != null ? c.getLecturerId() : "");
            String lName = (l != null) ? l.getTitledName() : "Unknown";
            Colors.printCenter(Colors.CYAN,
                    (i + 1) + ".  " + c.getCourseId()
                    + " – " + c.getTitle()
                    + "  [" + lName + "]");
        }
        Colors.divider();

        int idx = InputHelper.readInt("  Select course: ", 1, regIds.size());
        String courseId = regIds.get(idx - 1);
        Course course   = DataStore.courses.get(courseId);
        Lecturer lect   = DataStore.lecturers.get(course.getLecturerId());

        if (lect == null) { Colors.error("No lecturer for this course."); return; }

        // Block duplicate evaluation
        if (DataStore.hasEvaluated(student.getId(), lect.getId(), courseId)) {
            Colors.warn("You already evaluated " + lect.getTitledName()
                    + " for " + course.getTitle() + ".");
            return;
        }

        // Show lecturer info
        System.out.println();
        Colors.divider();
        lect.displayInfo();
        Colors.divider();

        // Rating scale
        Colors.printCenter(Colors.BOLD_WHITE, "Rate this lecturer:");
        Colors.printCenter(Colors.RED,          "1.  ★ Poor");
        Colors.printCenter(Colors.YELLOW,       "2.  ★★ Fair");
        Colors.printCenter(Colors.YELLOW,       "3.  ★★★ Good");
        Colors.printCenter(Colors.GREEN,        "4.  ★★★★ Very Good");
        Colors.printCenter(Colors.BOLD_GREEN,   "5.  ★★★★★ Excellent");
        int rating = InputHelper.readInt("  Your rating: ", 1, 5);

        String comment = InputHelper.readOptional(
                "  Add a comment (Enter to skip): ");

        // Save evaluation → HashMap → file
        Evaluation eval = new Evaluation(
                student.getId(), lect.getId(), courseId, rating, comment);
        DataStore.getOrCreateEvalList(lect.getId(), courseId).add(eval);
        DataStore.saveAll();

        Colors.success("Evaluation saved for " + lect.getTitledName());
        Colors.printCenter(Colors.BOLD_YELLOW,
                "Your rating: " + starsColored(rating));
    }

    /** View all evaluations for a chosen course */
    private void viewFeedback() {
        System.out.println();
        Colors.printCenter(Colors.BOLD_MAGENTA, "COURSE FEEDBACK");
        Colors.divider();

        List<Course> all = DataStore.getAllCoursesSorted();
        for (int i = 0; i < all.size(); i++) {
            Colors.printCenter(Colors.CYAN,
                    (i + 1) + ".  " + all.get(i).getCourseId()
                    + " – " + all.get(i).getTitle());
        }
        Colors.divider();

        int idx     = InputHelper.readInt("  Select course: ", 1, all.size());
        Course c    = all.get(idx - 1);
        Lecturer l  = DataStore.lecturers.get(c.getLecturerId());

        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_MAGENTA, "Feedback: " + c.getTitle());
        if (l != null) l.displayInfo();
        Colors.divider();

        List<Evaluation> evals =
                DataStore.getOrCreateEvalList(c.getLecturerId(), c.getCourseId());

        if (evals.isEmpty()) {
            Colors.info("No evaluations submitted yet.");
        } else {
            for (int i = 0; i < evals.size(); i++) {
                Evaluation e = evals.get(i);
                Colors.printCenter(Colors.YELLOW,
                        "[" + (i + 1) + "]  " + starsColored(e.getRating()));
                if (!e.getComment().isEmpty())
                    Colors.printCenter(Colors.WHITE, "    \"" + e.getComment() + "\"");
            }
            double avg = evals.stream()
                    .mapToInt(Evaluation::getRating).average().orElse(0);
            Colors.divider();
            Colors.printCenter(Colors.BOLD_YELLOW,
                    "Average Rating: " + String.format("%.2f", avg) + " / 5.00");
        }
        Colors.thickDivider();
    }

    /** Rank all lecturers by average rating across all evaluations */
    private void viewTopRated() {
        System.out.println();
        Colors.thickDivider();
        Colors.printCenter(Colors.BOLD_MAGENTA, "🏆  TOP-RATED LECTURERS");
        Colors.divider();

        // Build lecturerId → list of all their ratings
        Map<String, List<Integer>> allRatings = new HashMap<>();
        for (Map.Entry<String, ArrayList<Evaluation>> entry
                : DataStore.evaluations.entrySet()) {
            for (Evaluation e : entry.getValue()) {
                allRatings.computeIfAbsent(e.getLecturerId(), k -> new ArrayList<>())
                          .add(e.getRating());
            }
        }

        if (allRatings.isEmpty()) {
            Colors.info("No evaluations submitted yet.");
            Colors.thickDivider();
            return;
        }

        // Sort by average rating descending
        List<Map.Entry<String, List<Integer>>> sorted = new ArrayList<>(allRatings.entrySet());
        sorted.sort((a, b) -> {
            double avgA = a.getValue().stream().mapToInt(i -> i).average().orElse(0);
            double avgB = b.getValue().stream().mapToInt(i -> i).average().orElse(0);
            return Double.compare(avgB, avgA);
        });

        int rank = 1;
        for (Map.Entry<String, List<Integer>> entry : sorted) {
            Lecturer l = DataStore.lecturers.get(entry.getKey());
            String name = (l != null) ? l.getTitledName() : entry.getKey();
            double avg = entry.getValue().stream().mapToInt(i -> i).average().orElse(0);
            Colors.printCenter(Colors.CYAN,
                    rank++ + ".  " + name + "   "
                    + starsColored((int) Math.round(avg))
                    + "  " + String.format("(%.2f / 5.00)", avg));
        }
        Colors.thickDivider();
    }

    /** Colored star string: red≤2, yellow=3, green≥4 */
    private String starsColored(int rating) {
        String col = rating <= 2 ? Colors.RED
                : rating == 3 ? Colors.YELLOW : Colors.BOLD_GREEN;
        StringBuilder sb = new StringBuilder(col);
        for (int i = 1; i <= 5; i++) sb.append(i <= rating ? "★" : "☆");
        sb.append(Colors.RESET);
        return sb.toString();
    }
}
