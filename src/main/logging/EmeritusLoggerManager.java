package main.logging;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class EmeritusLoggerManager {
    private static final int MAX_LOG_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final int FLUSH_INTERVAL = 100; // Flush every 100 logs
    private static final ReentrantLock lock = new ReentrantLock();

    private static final String LOG_LEVEL_INFO = "INFO",
            LOG_LEVEL_DEBUG = "DEBUG",
            LOG_LEVEL_WARN = "WARN",
            LOG_LEVEL_ERROR = "ERROR";

    private static final String LOGGER_NAME = "logs/application";
    private static EmeritusLoggerManager mInstance = null;

    private File mLogFile;
    private String mLogLevel = LOG_LEVEL_INFO;
    private PrintWriter mPrintWriter;
    private int mLogCount = 0;
    private boolean mPersistInMemory = false;
    private boolean mConsoleLoggingEnabled = true;  // 🔹 Enable console logging by default
    private final List<String> mPersistenceStore = new ArrayList<>();

    private EmeritusLoggerManager() {
        try {
            File directory = new File("logs");
            directory.mkdir();
            mLogFile = createLogFile(LOGGER_NAME, String.valueOf(System.currentTimeMillis()));
            initLogFile();
        } catch (IOException ex) {
            System.err.println("LOGGER FAILED - INITIALIZATION EXCEPTION: " + ex.getMessage());
            mLogLevel = LOG_LEVEL_ERROR;
            mLogFile = null;
        }
    }

    public static EmeritusLoggerManager getInstance() {
        if (mInstance == null) {
            mInstance = new EmeritusLoggerManager();
        }
        return mInstance;
    }

    private void initLogFile() throws IOException {
        mPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(mLogFile, true), 1024));
    }

    public synchronized void setLogLevel(String level) {
        mLogLevel = level;
    }

    public void persistInMemory(boolean value) {
        mPersistInMemory = value;
    }

    public void enableConsoleLogging(boolean enabled) {
        mConsoleLoggingEnabled = enabled;
    }

    public void info(String reporter, String message, Object... args) {
        log(LOG_LEVEL_INFO, reporter, message, args);
    }

    public void debug(String reporter, String message, Object... args) {
        log(LOG_LEVEL_DEBUG, reporter, message, args);
    }

    public void warn(String reporter, String message, Object... args) {
        log(LOG_LEVEL_WARN, reporter, message, args);
    }

    public void error(String reporter, String message, Object... args) {
        log(LOG_LEVEL_ERROR, reporter, message, args);
    }

    private void log(String level, String reporter, String message, Object... args) {
        if (!shouldLog(level)) {
            return;
        }

        lock.lock();
        try {
            String formattedMessage = tryHandlingLogFormatting(level, reporter, message, args);

            if (mPersistInMemory) {
                mPersistenceStore.add(formattedMessage);
            }

            mPrintWriter.println(formattedMessage);
            mLogCount++;

            // 🔹 Also print to stdout if enabled
            if (mConsoleLoggingEnabled) {
                System.out.println(formattedMessage);
            }

            // Only flush periodically
            if (mLogCount >= FLUSH_INTERVAL) {
                flush();
                mLogCount = 0;
            }

            checkLogRotation();
        } finally {
            lock.unlock();
        }
    }

    private String tryHandlingLogFormatting(String level, String reporter, String message, Object... args) {
        return String.format("[%s] [%s] [%s] %s",
                getCurrentTime(), level, reporter, args == null ? message : StringFormatter.format(message, args));
    }

    private boolean shouldLog(String level) {
        return getLogLevelPriority(level) >= getLogLevelPriority(mLogLevel);
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

    private void checkLogRotation() {
        if (mLogFile.length() < MAX_LOG_SIZE) {
            return;
        }
        File rotatedFile = createLogFile(LOGGER_NAME, String.valueOf(System.currentTimeMillis()));
        if (mLogFile.renameTo(rotatedFile)) {
            try {
                initLogFile();
                System.out.println("LOGGER SUCCEED ROTATION");
            } catch (IOException e) {
                System.err.println("LOGGER FAILED - LOG ROTATION EXCEPTION: " + e.getMessage());
            }
        }
    }

    private File createLogFile(String name, String time) {
        return new File(name + "_" + time + ".log");
    }

    public List<String> getPersistenceStore() {
        return mPersistenceStore;
    }

    public synchronized void flush() {
        lock.lock();
        try {
            mPrintWriter.flush();
        } finally {
            lock.unlock();
        }
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static class StringFormatter {
        private static final StringBuilder result = new StringBuilder(100);

        public static String format(String template, Object... args) {
            if (template == null || args == null || args.length == 0) {
                return template;
            }

            result.delete(0, result.length());

            int argIndex = 0;
            int lastIndex = 0;

            while (lastIndex < template.length()) {
                int openIndex = template.indexOf("{}", lastIndex);
                if (openIndex == -1 || argIndex >= args.length) {
                    result.append(template, lastIndex, template.length());
                    break;
                }

                result.append(template, lastIndex, openIndex);
                result.append(args[argIndex++]);
                lastIndex = openIndex + 2;
            }

            return result.toString();
        }
    }
}