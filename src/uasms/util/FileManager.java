package uasms.util;

import java.io.*;
import java.util.HashMap;
/**
 * FileManager.java
 * ─────────────────────────────────────────────────────
 * Handles ALL file reading and writing for the app.
 * Uses Java Object Serialization to save HashMaps to
 * binary .dat files in the /data/ folder.
 *
 * Files created:
 *   data/students.dat        → student profile data
 *   data/credentials.dat     → username → password
 *   data/admins.dat          → admin accounts
 *   data/admin_passwords.dat → admin username → password
 *   data/courses.dat         → course data
 *   data/lecturers.dat       → lecturer data
 *   data/registrations.dat   → studentId → Set of courseIds
 *   data/attendance.dat      → "sid_cid" → AttendanceRecord
 *   data/evaluations.dat     → "lid_cid" → list of Evaluations
 * ─────────────────────────────────────────────────────
 */
public class FileManager {

    private static final String DATA_DIR = "data/";

    // ── File path constants ───────────────────────────
    public static final String STUDENTS        = DATA_DIR + "students.dat";
    public static final String CREDENTIALS     = DATA_DIR + "credentials.dat";
    public static final String ADMINS          = DATA_DIR + "admins.dat";
    public static final String ADMIN_PASSWORDS = DATA_DIR + "admin_passwords.dat";
    public static final String COURSES         = DATA_DIR + "courses.dat";
    public static final String LECTURERS       = DATA_DIR + "lecturers.dat";
    public static final String REGISTRATIONS   = DATA_DIR + "registrations.dat";
    public static final String ATTENDANCE      = DATA_DIR + "attendance.dat";
    public static final String EVALUATIONS     = DATA_DIR + "evaluations.dat";

    /** Create the /data/ directory if it doesn't exist yet. */
    public static void initDataFolder() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Save any Serializable object to a .dat file.
     */
    public static void save(String filePath, Object data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {oos.writeObject(data);
        } catch (IOException e) {
            Colors.error("Save failed [" + filePath + "]: " + e.getMessage());
        }
    }

    /**
     * Load a Serializable object from a .dat file.
     * Returns null if the file doesn't exist yet.
     */
    public static Object load(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return null;
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Colors.error("Load failed [" + filePath + "]: " + e.getMessage());
            return null;
        }
    }

    /**
     * Load a file and return it as a HashMap<String, V>.
     * Returns an empty HashMap if the file doesn't exist.
     */
    @SuppressWarnings("unchecked")
    public static <V> HashMap<String, V> loadMap(String filePath) {
        Object obj = load(filePath);
        return (obj instanceof HashMap) ? (HashMap<String, V>) obj : new HashMap<>();
    }

    /** Save a HashMap to a .dat file. */
    public static <V> void saveMap(String filePath, HashMap<String, V> map) {
        save(filePath, map);
    }

    /** Check if a data file exists (used to detect first run). */
    public static boolean exists(String filePath) {
        return new File(filePath).exists();
    }
}
