package uasms.util;

/**
 * Colors.java
 * ─────────────────────────────────────────────────────
 * ANSI color codes + robust centered text printing.
 *
 * Windows terminal fix:
 *   - All symbols use safe ASCII/UTF-8 characters
 *   - Centering strips ANSI codes before measuring width
 *   - Console width fixed at 60 chars (works in CMD,
 *     PowerShell, Windows Terminal, IntelliJ)
 * ─────────────────────────────────────────────────────
 */
public class Colors {

    // Console width in characters (safe for most terminals)
    public static final int WIDTH = 60;

    // ── Reset ─────────────────────────────────────────
    public static final String RESET        = "\u001B[0m";

    // ── Regular Colors ────────────────────────────────
    public static final String RED          = "\u001B[31m";
    public static final String GREEN        = "\u001B[32m";
    public static final String YELLOW       = "\u001B[33m";
    public static final String BLUE         = "\u001B[34m";
    public static final String MAGENTA      = "\u001B[35m";
    public static final String CYAN         = "\u001B[36m";
    public static final String WHITE        = "\u001B[37m";

    // ── Bold Colors ───────────────────────────────────
    public static final String BOLD_RED     = "\u001B[1;31m";
    public static final String BOLD_GREEN   = "\u001B[1;32m";
    public static final String BOLD_YELLOW  = "\u001B[1;33m";
    public static final String BOLD_BLUE    = "\u001B[1;34m";
    public static final String BOLD_MAGENTA = "\u001B[1;35m";
    public static final String BOLD_CYAN    = "\u001B[1;36m";
    public static final String BOLD_WHITE   = "\u001B[1;37m";

    // ═════════════════════════════════════════════════
    //  CENTERING CORE
    // ═════════════════════════════════════════════════

    /**
     * Strip ANSI escape codes to get the real visible length.
     */
    public static String stripAnsi(String s) {
        return s == null ? "" : s.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    /**
     * Build a centered string: adds spaces on the left so
     * the visible text appears centered within WIDTH columns.
     */
    public static String centerText(String text) {
        int visible = stripAnsi(text).length();
        int pad     = Math.max(0, (WIDTH - visible) / 2);
        return " ".repeat(pad) + text;
    }

    /**
     * Print a line of colored text, centered on screen.
     * @param color  ANSI color constant (no color codes inside text)
     * @param text   plain visible text
     */
    public static void printCenter(String color, String text) {
        int pad = Math.max(0, (WIDTH - text.length()) / 2);
        System.out.println(" ".repeat(pad) + color + text + RESET);
    }

    /**
     * Print plain centered text (no color).
     */
    public static void printCenter(String text) {
        System.out.println(centerText(text));
    }

    // ═════════════════════════════════════════════════
    //  DIVIDERS  (centered, full width)
    // ═════════════════════════════════════════════════

    public static void thickDivider() {
        System.out.println(BOLD_BLUE + "=" .repeat(WIDTH) + RESET);
    }

    public static void divider() {
        System.out.println(BLUE + "-".repeat(WIDTH) + RESET);
    }

    public static void starDivider() {
        System.out.println(BOLD_YELLOW + "*".repeat(WIDTH) + RESET);
    }

    // ═════════════════════════════════════════════════
    //  SEMANTIC HELPERS  (all centered)
    // ═════════════════════════════════════════════════

    public static void success(String msg) { printCenter(BOLD_GREEN,  "[OK] " + msg); }
    public static void error(String msg)   { printCenter(BOLD_RED,    "[!!] " + msg); }
    public static void warn(String msg)    { printCenter(BOLD_YELLOW, "[>>] " + msg); }
    public static void info(String msg)    { printCenter(CYAN,        "[i]  " + msg); }

    // ═════════════════════════════════════════════════
    //  MENU & FIELD HELPERS
    // ═════════════════════════════════════════════════

    /**
     * Print a centered menu item line.
     * Example:  printMenuItem(GREEN, "1", "Course Registration")
     * Output:        1.  Course Registration
     */
    public static void printMenuItem(String color, String number, String label) {
        String line = number + ".  " + label;
        int pad = Math.max(0, (WIDTH - line.length()) / 2);
        System.out.println(" ".repeat(pad)
                + color + number + ".  " + RESET + WHITE + label + RESET);
    }

    /**
     * Print a centered key-value pair.
     * Example:  printField("Full Name", "NGASSA Fredy")
     * Output:        Full Name : NGASSA Fredy
     */
    public static void printField(String label, String value) {
        String line = label + " : " + value;
        int pad = Math.max(0, (WIDTH - line.length()) / 2);
        System.out.println(" ".repeat(pad)
                + YELLOW + label + " : " + WHITE + value + RESET);
    }

    /**
     * Print a centered table row with fixed column widths.
     * Used for course and student tables.
     */
    public static void printRow(String col1, int w1,
                                String col2, int w2,
                                String col3, int w3) {
        String line = String.format("%-" + w1 + "s %-" + w2 + "s %-" + w3 + "s",
                col1, col2, col3);
        int pad = Math.max(0, (WIDTH - line.length()) / 2);
        System.out.println(" ".repeat(pad) + line);
    }
}
