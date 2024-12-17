package main.logging;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ELoggerManager {

    private PrintWriter mPrintWriter = null;
    private final StringBuilder mStringBuilder = new StringBuilder();
    private static final String LOGGER_TOKEN = "{}";
    private static final int MAX_LOG_SIZE = 10 * 1024 * 1024; // 10 MB
    public static final String LOG_LEVEL_INFO = "INFO";
    public static final String LOG_LEVEL_DEBUG = "DEBUG";
    public static final String LOG_LEVEL_WARN = "WARN";
    public static final String LOG_LEVEL_ERROR = "ERROR";
    private String logLevel = LOG_LEVEL_INFO;

    public ELoggerManager() {
        try {
            initLogFile();
        } catch (Exception ex) {
            System.err.println("LOGGER FAILED - INITIALIZATION EXCEPTION");
            logLevel = LOG_LEVEL_ERROR;
        }
    }

    private void initLogFile() throws IOException {
        FileOutputStream fos = new FileOutputStream(FileDescriptor.out);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(osw, 512);
        mPrintWriter = new PrintWriter(bw);
    }

    public synchronized void setLogLevel(String level) {
        logLevel = level;
    }

    public void info(String message) {
        log(LOG_LEVEL_INFO, "", message);
    }

    public void info(String reporter, String message, Object... args) {
        log(LOG_LEVEL_INFO, reporter, message, args);
    }

    public void debug(String message) {
        log(LOG_LEVEL_DEBUG, "", message);
    }

    public void debug(String reporter, String message, Object... args) {
        log(LOG_LEVEL_DEBUG, reporter, message, args);
    }

    public void warn(String message) {
        log(LOG_LEVEL_WARN, "", message);
    }

    public void warn(String reporter, String message, Object... args) {
        log(LOG_LEVEL_WARN, reporter, message, args);
    }

    public void error(String message) {
        log(LOG_LEVEL_ERROR, "", message);
    }

    public void error(String reporter, String message, Object... args) {
        log(LOG_LEVEL_ERROR, reporter, message, args);
    }

    private synchronized void log(String level, String reporter, String message, Object... args) {
        if (!shouldLog(level)) {
            return;
        }

        String toLog = format(
                "{} {} {} {}",
                "[" + getCurrentTime() + "]", "[" + level + "]", "[" + reporter + "]", format(message, args)
        );
        mStringBuilder.append(toLog).append(System.lineSeparator());

        mPrintWriter.println(toLog);
        mPrintWriter.flush();

        rotateLogFile();
    }

    private boolean shouldLog(String level) {
        return getLogLevelPriority(level) >= getLogLevelPriority(logLevel);
    }

    private int getLogLevelPriority(String level) {
        return switch (level) {
            case LOG_LEVEL_ERROR -> 3;
            case LOG_LEVEL_WARN -> 2;
            case LOG_LEVEL_INFO -> 1;
            case LOG_LEVEL_DEBUG -> 0;
            default -> Integer.MIN_VALUE;
        };
    }

    private void rotateLogFile() {
        File logFile = new File(getClass().getSimpleName() + ".log");
        if (logFile.length() > MAX_LOG_SIZE) {
            File rotatedFile = new File(getClass().getSimpleName() + "_" + System.currentTimeMillis() + ".log");
            logFile.renameTo(rotatedFile);
        }
    }

    public synchronized void flush() {
        try {
            String fileName = getClass().getSimpleName() + ".log";
            PrintWriter out = new PrintWriter(new FileWriter(fileName, true), true);
            out.write(mStringBuilder.toString());
            out.close();
            mStringBuilder.setLength(0);
        } catch (Exception ex) {
            System.err.println("LOGGER FAILED - FLUSHING EXCEPTION");
        }
    }

    private String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
    /**
     * Formats a string by replacing "{}" placeholders with values from the provided array.
     *
     * @param format The format string containing "{}" placeholders.
     * @param args   The array of strings to replace the placeholders.
     * @return The formatted string with placeholders replaced.
     */
    private static String format(String format, Object... args) {
        if (format == null || args == null) {
            throw new IllegalArgumentException("Format string and arguments must not be null.");
        }

        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        int startIndex = 0;

        // Iterate through the format string to replace placeholders
        while (startIndex < format.length()) {
            int openIndex = format.indexOf(LOGGER_TOKEN, startIndex);
            if (openIndex == -1) {
                // No more placeholders, append the rest of the string
                result.append(format.substring(startIndex));
                break;
            }

            // Append the part before the placeholder
            result.append(format, startIndex, openIndex);

            // Replace the placeholder with the next argument
            if (argIndex < args.length) {
                result.append(args[argIndex++]);
            } else {
                result.append("{}"); // If no arguments are left, keep the placeholder
            }

            // Move the starting index past the current placeholder
            startIndex = openIndex + 2;
        }

        return result.toString();
    }
}