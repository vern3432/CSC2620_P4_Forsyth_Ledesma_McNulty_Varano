package com.gutenberg;


import java.util.regex.Pattern;

/**
 * Utility class containing predefined regular expression patterns for common English word endings.
 * These patterns can be used for filtering words or strings based on their endings.
 *
 * @author Maddie
 */
public class Filters {

    /**
     * Regular expression pattern for words ending with "ing".
     */
    public static final Pattern ingPattern = Pattern.compile(".*ing$");

    /**
     * Regular expression pattern for words containing "ough" anywhere.
     */
    public static final Pattern oughPattern = Pattern.compile(".*ough.*");

    /**
     * Regular expression pattern for words ending with "ism".
     */
    public static final Pattern ismPattern = Pattern.compile(".*ism$");

    /**
     * Regular expression pattern for words starting with "kn".
     */
    public static final Pattern knPattern = Pattern.compile("^kn.*");

    /**
     * Regular expression pattern for words containing "augh" anywhere.
     */
    public static final Pattern aughPattern = Pattern.compile(".*augh.*");

    /**
     * Private constructor to prevent instantiation of this class.
     * All fields are static, so instances are not needed.
     */
    private Filters() {
    }
}