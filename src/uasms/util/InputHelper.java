package uasms.util;

import java.util.Scanner;

/**
 * InputHelper.java
 * ─────────────────────────────────────────────────────
 * Safe user input utilities.
 *
 * NOTE ON PASSWORDS (Windows fix):
 *   System.console() returns null when running a JAR
 *   in Windows CMD / PowerShell, which causes password
 *   input to be silently swallowed.
 *   Solution: always use Scanner for input on all platforms.
 *   The password is visible while typing — this is normal
 *   and acceptable for a console student project.
 * ─────────────────────────────────────────────────────
 */
public class InputHelper {

    // One shared Scanner for the whole application
    private static final Scanner scanner = new Scanner(System.in);

    // ── String input ──────────────────────────────────

    /**
     * Read a non-empty trimmed string.
     * Keeps asking until the user types something.
     */
    public static String readString(String prompt) {
        String input = "";
        while (input.isEmpty()) {
            System.out.print(Colors.BOLD_WHITE + prompt + Colors.RESET);
            System.out.flush();
            input = scanner.nextLine().trim();
            if (input.isEmpty()) Colors.error("Input cannot be empty.");
        }
        return input;
    }

    /**
     * Read an optional string (empty is allowed).
     * Used for comments, press-Enter-to-continue, etc.
     */
    public static String readOptional(String prompt) {
        System.out.print(Colors.BOLD_WHITE + prompt + Colors.RESET);
        System.out.flush();
        return scanner.nextLine().trim();
    }

    // ── Integer input ─────────────────────────────────

    /**
     * Read an integer within [min, max].
     * Loops until a valid in-range number is entered.
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(Colors.BOLD_WHITE + prompt + Colors.RESET);
            System.out.flush();
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) return value;
                Colors.error("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                Colors.error("Invalid input — please enter a whole number.");
            }
        }
    }

    // ── Password input ────────────────────────────────

    /**
     * Read a password using plain Scanner (works on all platforms).
     * Enforces minimum 8 characters.
     *
     * Why not System.console()?
     *   System.console() returns null when running a JAR in
     *   Windows CMD/PowerShell, causing input to be lost entirely.
     *   Using Scanner directly works everywhere: CMD, PowerShell,
     *   Windows Terminal, IntelliJ, macOS, Linux.
     */
    public static String readPassword(String prompt) {
        while (true) {
            System.out.print(Colors.BOLD_WHITE + prompt + Colors.RESET);
            System.out.flush();
            String password = scanner.nextLine().trim();
            if (password.length() < 8) {
                Colors.error("Password must be at least 8 characters.");
            } else {
                return password;
            }
        }
    }

    /**
     * Read a new password with confirmation.
     * Both entries must match and be at least 8 characters.
     */
    public static String readNewPassword(String prompt) {
        while (true) {
            String p1 = readPassword(prompt);
            String p2 = readPassword("Confirm password      : ");
            if (p1.equals(p2)) return p1;
            Colors.error("Passwords do not match. Please try again.");
        }
    }

    /** Close the scanner when the application exits. */
    public static void close() {
        scanner.close();
    }
}