package main.logging;

import main.utils.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static main.utils.StringFormatter.format;

public class ELoggerManager {

    private PrintWriter outputStream = null;
    private final StringBuilder buffer = new StringBuilder();
    private final static String LOGGER_TOKEN = "{}";

    public ELoggerManager() {
        try {
            FileOutputStream fos = new FileOutputStream(FileDescriptor.out);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter bw = new BufferedWriter(osw, 512);
            outputStream = new PrintWriter(bw);
        } catch (Exception ex) {
            System.err.println("LOGGER FAILED - INITIALIZATION EXCEPTION");
        }
    }

    public void debug(String message) { debug("", "", message); }
    public void debug(String reporter, String message, Object... args) { log("DEBUG", reporter, message, args); }

    public void info(String message) { info("", "", message); }
    public void info(String reporter, String message, Object... args) { log("INFO", reporter, message, args); }

    public void warn(String message) { warn("", "", message); }
    public void warn(String reporter, String message, Object... args) {  log("WARN", reporter, message, args); }

    public void error(String message) { error("", "", message); }
    public void error(String reporter, String message, Object... args) {  log("ERROR", reporter, message, args); }

    private void log(String level, String reporter, String message, Object... args) {
        String toLog = format("{} {} {}", "[" + level + "]", "[" + reporter + "]", format(message, args));
        buffer.append(toLog).append(System.lineSeparator());
        outputStream.println(toLog);
        outputStream.flush();
    }

    public void flush() {
        try {
            String fileName = getClass().getSimpleName() + ".log";
            PrintWriter out = new PrintWriter(new FileWriter(fileName, false), true);
            out.write(buffer.toString());
            out.close();
            buffer.delete(0, buffer.length());
        } catch (Exception ex) {
            System.err.println("LOGGER FAILED - FLUSHING EXCEPTION");
        }
    }
}