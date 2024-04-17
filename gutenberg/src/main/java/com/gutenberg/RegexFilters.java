package com.gutenberg;


import java.util.regex.Pattern;

/**
 * Utility class containing predefined regular expression patterns for common English word endings.
 * These patterns can be used for filtering words or strings based on their endings.
 *
 * @author Maddie
 */
public class RegexFilters {

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
     * Regular expression pattern for words starting with "pre".
     */
    public static final Pattern prePattern = Pattern.compile("^pre.*");

    /**
     * Regular expression pattern for words containing "augh" anywhere.
     */
    public static final Pattern aughPattern = Pattern.compile(".*augh.*");

    /**
     * Define the regex pattern for extracting authors' names
     */
    public static final Pattern authorPattern = Pattern.compile("(?i)Author:\\s*([^\\n]+)");


    /**
     * Private constructor to prevent instantiation of this class.
     * All fields are static, so instances are not needed.
     */
    private RegexFilters() {
    }
}