package main.logging;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static main.utils.StringFormatter.format;

public class ELoggerManager {

    private PrintWriter mPrintWriter = null;
    private final StringBuilder mStringBuilder = new StringBuilder();
    private final static String LOGGER_TOKEN = "{}";
    public static String LOG_LEVEL_INFO = "INFO",
            LOG_LEVEL_DEBUG = "DEBUG",
            LOG_LEVEL_WARN = "WARN",
            LOG_LEVEL_ERROR = "ERROR";
    private String logLevel = LOG_LEVEL_INFO;
    public ELoggerManager() {
        try {
            FileOutputStream fos = new FileOutputStream(FileDescriptor.out);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter bw = new BufferedWriter(osw, 512);
            mPrintWriter = new PrintWriter(bw);
            logLevel = LOG_LEVEL_INFO;
        } catch (Exception ex) {
            System.err.println("LOGGER FAILED - INITIALIZATION EXCEPTION");
            logLevel = "ERROR";
        }
    }


    public void setLogLevel(String level) { logLevel = level; }
    public void info(String message) { info("", "", message); }
    public void info(String reporter, String message, Object... args) { log("INFO", reporter, message, args); }
    public void debug(String message) { debug("", "", message); }
    public void debug(String reporter, String message, Object... args) { log("DEBUG", reporter, message, args); }
    public void warn(String message) { warn("", "", message); }
    public void warn(String reporter, String message, Object... args) {  log("WARN", reporter, message, args); }

    public void error(String message) { error("", "", message); }
    public void error(String reporter, String message, Object... args) {  log("ERROR", reporter, message, args); }

    private void log(String level, String reporter, String message, Object... args) {


        String toLog = format("{} {} {}", "[" + level + "]", "[" + reporter + "]", format(message, args));
        mStringBuilder.append(toLog).append(System.lineSeparator());

        if (level.equalsIgnoreCase("INFO")) {
            if (logLevel.equalsIgnoreCase("DEBUG")) {
                return;
            } else if (logLevel.equalsIgnoreCase("WARN")) {
                return;
            } else if (logLevel.equalsIgnoreCase("ERROR")) {
                return;
            }
        } else if (level.equalsIgnoreCase("DEBUG")) {
            if (logLevel.equalsIgnoreCase("WARN")) {
                return;
            } else if (logLevel.equalsIgnoreCase("ERROR")) {
                return;
            }
        } else if (level.equalsIgnoreCase("WARN")) {
            if (logLevel.equalsIgnoreCase("ERROR")) {
                return;
            }
        }

        mPrintWriter.println(toLog);
        mPrintWriter.flush();
    }

    public void flush() {
        try {
            String fileName = getClass().getSimpleName() + ".log";
            PrintWriter out = new PrintWriter(new FileWriter(fileName, false), true);
            out.write(mStringBuilder.toString());
            out.close();
            mStringBuilder.delete(0, mStringBuilder.length());
        } catch (Exception ex) {
            System.err.println("LOGGER FAILED - FLUSHING EXCEPTION");
        }
    }
}